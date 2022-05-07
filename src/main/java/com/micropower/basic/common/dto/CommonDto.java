package com.micropower.basic.common.dto;

import com.micropower.basic.common.AreaEnum;
import com.micropower.basic.common.DtoEnum;
import com.micropower.basic.entity.DeviceBean;
import com.micropower.basic.entity.StationBean;
import com.micropower.basic.service.CompanyService;
import com.micropower.basic.service.OperationRecordService;
import com.micropower.basic.service.StationService;
import com.micropower.basic.timer.PublicProcessing;
import com.micropower.basic.timer.RealtimeStateTask;
import com.micropower.basic.util.DecoderUtil;
import com.micropower.basic.util.RedisUtil;
import com.micropower.basic.util.StaticFinalWard;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2021/2/9 9:57
 * @Description: TODO → 协议数据解析父类
 * @Author:Kohaku_川
 **/
@Slf4j
@Data
@Component
public class CommonDto {

    private static CommonDto dto;

    private @Autowired
    CompanyService companyService;
    private @Autowired
    RedisUtil redisUtil;
    private @Autowired
    OperationRecordService operationRecordService;
    private @Autowired
    StationService stationService;

    @PostConstruct
    public void init() {
        dto = this;
        dto.redisUtil = this.redisUtil;
        dto.companyService = this.companyService;
        dto.operationRecordService = this.operationRecordService;
        dto.stationService = this.stationService;
    }

    //帧头
    private static final String HEAD_STR = "aa55";

    //帧尾
    private static final String END_STR = "a55a";

    /**
     * 下发反馈-是否成功，标识码
     */
    private static final String[] FEEDBACK_CODE = {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "10", "11", "1E", "24", "28", "29", "2B"};

    /**
     * 下发返回具体报文，标识码
     */
    private static final String[] QUERY_BACK_CODE = {"C9", "CA", "CB", "CC", "25"};

    /**
     * 帧协议版本
     */
    private String version;

    /**
     * 帧长度
     */
    private Integer length;

    /**
     * 指令标识码
     */
    private String code;

    /**
     * 设备地区Code
     */
    private String areaCode;

    /**
     * 设备地区
     */
    private String area;

    /**
     * 是否是控制返回指令
     */
    private boolean feedback;

    /**
     * 控制返回
     */
    private boolean success;

    /**
     * 设备地址号
     */
    private Integer address;

    /**
     * 通讯方式
     */
    private String communicationMode;

    public static String[] getQueryBackCode() {
        return QUERY_BACK_CODE;
    }

    public static CommonDto getDecode(String in) {
        try {
            //2字节帧头
            String head = in.substring(0, 4);
            //2字节帧尾
            String end = in.substring(in.length() - 4);
            //1字节校验
            String checkCode = in.substring(in.length() - 6, in.length() - 4);
            //计算校验
            String check = DecoderUtil.makeCheckSum(in.substring(0, in.length() - 6));
            if (HEAD_STR.equalsIgnoreCase(head) && END_STR.equalsIgnoreCase(end) && checkCode.equalsIgnoreCase(check)) {
                //1字节版本
                String version = in.substring(4, 6);
                //2字节帧长
                int length = Integer.parseInt(in.substring(6, 10), 16);
                //1字节指令码
                String code = in.substring(10, 12).toUpperCase();
                DtoEnum dtoEnum = DtoEnum.find(code);
                //2字节地区码
                String areaCodeStr = in.substring(12, 16);
                String areaCode = String.format("%04d", Integer.parseInt(areaCodeStr, 16));
                AreaEnum areaEnum = AreaEnum.find(areaCode);
                String area = "未知";
                if (areaEnum != null) {
                    area = areaEnum.getMsg();
                }
                //2字节设备地址
                Integer address = Integer.parseInt(in.substring(16, 20), 16);
                DeviceBean device = dto.companyService.getByAddress(areaCode, address);
                if (device != null) {
                    dto.companyService.dataUpload(areaCode, address);
                    StationBean station = PublicProcessing.getStationByAreaAddress(areaCode, address);
                    //过滤存在设备且状态为启用
                    if (StaticFinalWard.ON.equals(device.getState()) && station != null) {
                        //主动上传报文
                        if (dtoEnum != null) {
                            CommonDto dto = (CommonDto) dtoEnum.getClss().newInstance();
                            dto.setVersion(version);
                            dto.setArea(area);
                            dto.setAreaCode(areaCode);
                            dto.setCode(code);
                            dto.setLength(length);
                            dto.setFeedback(false);
                            dto.setSuccess(true);
                            dto.setAddress(address);
                            dto.decode(in.substring(20, in.length() - 6), dto);
                            return dto;
                            //下发指令后得到的反馈报文
                        } else if (Arrays.asList(FEEDBACK_CODE).contains(code)) {
                            CommonDto dto = new CommonDto();
                            dto.setVersion(version);
                            dto.setArea(area);
                            dto.setAreaCode(areaCode);
                            dto.setCode(code);
                            dto.setLength(length);
                            dto.setFeedback(true);
                            dto.setAddress(address);
                            //1字节反馈结果
                            int feedback = Integer.parseInt(in.substring(20, 22));
                            dto.setSuccess(feedback == 1);
                            return dto;
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void decode(String in, CommonDto commonDto) {
    }

    public ByteBuf getEncode(ByteBuf out) {
        StringBuilder outStr = new StringBuilder();
        //帧头
        outStr.append(HEAD_STR)
                //帧协议版本
                .append(redisUtil.hget("versionList", this.getAreaCode() + this.getAddress()))
                //帧长度
                .append(String.format("%04X", this.getLength()))
                //指令码
                .append(this.getCode())
                //地区代码
                .append(String.format("%04X", Integer.valueOf(this.getAreaCode())))
                //设备地址
                .append(String.format("%04X", this.getAddress()));
        this.encode(out, outStr);
        return out;
    }

    public void encode(ByteBuf out, StringBuilder outStr) {
        //计算校验
        String check = DecoderUtil.makeCheckSum(outStr.toString());
        //检验和
        outStr.append(check)
                //帧尾
                .append(END_STR);
        Map<String, Object> map = new HashMap<>();
        map.put("time", new Date());
        map.put("content", outStr.toString().toUpperCase());
        map.put("type", this.getCommunicationMode());
        map.put("areaCode", this.getAreaCode());
        map.put("address", this.getAddress());
        map.put("description", "指令发送，参数解析见控制记录");
        operationRecordService.insertMessage(map);
        out.writeBytes(DecoderUtil.hexStr2bytes(outStr.toString().toUpperCase()));
    }

}
