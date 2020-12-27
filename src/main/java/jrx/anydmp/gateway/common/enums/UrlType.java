package jrx.anydmp.gateway.common.enums;

/**
 * @author peidong.meng
 * @date 2018/4/17
 */
public enum UrlType {

    Pass("忽略"),
    Login("登陆"),
    Index("首页"),
    NeedAuth("需要鉴权");

    private String describe;

    UrlType(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
