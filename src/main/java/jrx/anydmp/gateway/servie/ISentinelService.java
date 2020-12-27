package jrx.anydmp.gateway.servie;

import jrx.anydmp.gateway.sentinel.SentinelResource;

public interface ISentinelService {

    /**
     * 降级配置规则说明
     * 1.根据平均响应时间 (DEGRADE_GRADE_RT)：当资源的平均响应时间超过阈值（DegradeRule 中的 count，以 ms 为单位）之后，
     * 资源进入准降级状态。接下来如果持续进入 5 个请求，它们的 RT 都持续超过这个阈值，
     * 那么在接下的时间窗口（DegradeRule 中的 timeWindow，以 s 为单位）之内，对这个方法的调用都会自动地返回。
     *
     * 2.根据异常比例 (DEGRADE_GRADE_EXCEPTION)：当资源的每秒异常总数占通过总数的比值超过阈值（DegradeRule 中的 count）之后，
     * 资源进入降级状态，即在接下的时间窗口（DegradeRule 中的 timeWindow，以 s 为单位）之内，对这个方法的调用都会自动地返回。
     * @param sentinelResource
     */
    void setRule(SentinelResource sentinelResource);

    void removeRule(String resourceId, String type);

    void loadOnStartup();

}
