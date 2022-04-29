package com.micropower.basic.common;

/**
 * @Author kohaku_C
 * @Description //TODO 地区编号-地区名称 枚举类
 * @Date 10:30  2021/2/20
 * @Param
 * @return
 **/
public enum AreaEnum {
    hangzhou("0571", "杭州"), huzhou("0572", "湖州"), jiaxing("0573", "嘉兴"), ningbo("0574", "宁波"), shaoxing("0575", "绍兴"),
    taizhou("0576", "台州"), wenzhou("0577", "温州"), lishui("0578", "丽水"), jinhua("0579", "金华"), zhoushan("0580", "舟山"),
    tianjin("0022", "天津"), suzhou("0512", "苏州");

    AreaEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

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

    public static AreaEnum find(String code) {
        for (AreaEnum Enum : AreaEnum.values()) {
            if (Enum.code.equals(code)) {
                return Enum;
            }
        }
        return null;
    }
}
