package com.micropower.basic.common;


import com.micropower.basic.common.dto.receive.*;
import com.micropower.basic.common.dto.send.QuerySettingDto;

/**
 * @Author kohaku_C
 * @Description //TODO 报文标识码 枚举类
 * @Date 10:31  2021/2/20
 */
public enum DtoEnum {
    RUNNING_STATE("c0", "运行状态", RunningStateDto.class),
    QUERY_SETTING("CC", "查询参数返回", QuerySettingBackDto.class),
    OPERATION_BACK("C9", "查询操作记录返回", OperationRecordDto.class),
    EXCEPTION_BACK("CA", "查询异常记录返回", ExceptionRecordDto.class),
    HISTORY_BACK("CB", "查询历史记录返回", HistoryRecordDto.class),
    QUERY_UPGRADE_BACK("25", "查询当前文件传输包返回", QueryUpgradeVersionBack.class),
    CYCLE_RECORD("D1", "周期上报数据", ValueRecordDto.class);


    DtoEnum(String code, String msg, Class<?> clss) {
        this.code = code;
        this.msg = msg;
        this.clss = clss;
    }

    private String code;
    private String msg;
    private Class<?> clss;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static DtoEnum find(String code) {
        for (DtoEnum dtoEnum : DtoEnum.values()) {
            if (dtoEnum.code.equals(code)) {
                return dtoEnum;
            }
        }
        return null;
    }

    public Class<?> getClss() {
        return clss;
    }

    public void setClss(Class<?> clss) {
        this.clss = clss;
    }
}
