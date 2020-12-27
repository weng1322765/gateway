package jrx.anydmp.gateway.sentinel;

import java.io.Serializable;

public class SentinelResource implements Serializable {
    private static final long serialVersionUID = 660304432821079260L;

    private String resourceId;
    private String type;
    private String url;
    private Double count;
    private Integer grade;
    private Integer maxQueueTime;
    //degrade only
    private Integer time;//unit:ms
    private Integer flowMode;//流控模式，直连，关联，链路 strategy
    private Integer flowMethod;//流控方式，快速失败，warmup，排队 controlBehavior

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getFlowMethod() {
        return flowMethod;
    }

    public void setFlowMethod(Integer flowMethod) {
        this.flowMethod = flowMethod;
    }

    public Integer getFlowMode() {
        return flowMode;
    }

    public void setFlowMode(Integer flowMode) {
        this.flowMode = flowMode;
    }

    public Integer getMaxQueueTime() {
        return maxQueueTime;
    }

    public void setMaxQueueTime(Integer maxQueueTime) {
        this.maxQueueTime = maxQueueTime;
    }
}
