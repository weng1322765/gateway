package jrx.anydmp.gateway.common.enums;

/**
 * Created by babysbreath on 2017/9/6.
 */
public enum AuthValidStatus {
    EXPIRED("0","token已经过期"),
    VALID("1","token有效"),
    ACCESS("2","有权限可访问"),
    DENY("3","拒绝访问"),
    STOP("4","服务停用"),
    MUTILOGIN("5","异地登录冲突");

    private String value;
    private String name;

    AuthValidStatus() {
    }

    AuthValidStatus(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
