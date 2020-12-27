package jrx.anydmp.gateway.sentinel;

import java.io.Serializable;

public class SentinelBlockLog implements Serializable {
    private static final long serialVersionUID = 67499572358992459L;

    private String datetime;//时间
    private String resource;
    private String blockType;//类型，限流、降级、系统保护
    private String blockCount;//被拦截数量

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public String getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(String blockCount) {
        this.blockCount = blockCount;
    }
}
