package jrx.anydmp.gateway.controller;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.log.LogBase;
import com.alibaba.csp.sentinel.node.metric.MetricWriter;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.csp.sentinel.util.PidUtil;
import jrx.anytxn.common.data.TxnRespResult;
import jrx.anydmp.gateway.sentinel.SentinelLog;
import jrx.anydmp.gateway.sentinel.SentinelResource;
import jrx.anydmp.gateway.servie.ISentinelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class SentinelController {

    @Autowired
    private ISentinelService sentinelService;


    /**
     * 根据具体请求url添加限流规则接口，暂时不包括系统规则(SystemRule)
     * @param
     * @return
     */
    @GetMapping("setRule")
    public Mono<TxnRespResult> setRule(ServerWebExchange exchange) {
        sentinelService.setRule(initResource(exchange));
        return Mono.just(new TxnRespResult().getSuccess(null));
    }


    private SentinelResource initResource(ServerWebExchange exchange) {
        SentinelResource sentinelResource = new SentinelResource();
        sentinelResource.setResourceId(exchange.getRequest().getQueryParams().getFirst("resource"));
        sentinelResource.setUrl(sentinelResource.getResourceId());
        sentinelResource.setType(exchange.getRequest().getQueryParams().getFirst("type"));
        if (exchange.getRequest().getQueryParams().getFirst("count") != null) {
            sentinelResource.setCount(Double.valueOf(exchange.getRequest().getQueryParams().getFirst("count")));
        }
        if (exchange.getRequest().getQueryParams().getFirst("time") != null) {
            sentinelResource.setTime(Integer.valueOf(exchange.getRequest().getQueryParams().getFirst("time")));
        }
        if (exchange.getRequest().getQueryParams().getFirst("grade") != null) {
            sentinelResource.setGrade(Integer.valueOf(exchange.getRequest().getQueryParams().getFirst("grade")));
        }
        if (exchange.getRequest().getQueryParams().getFirst("flowMode") != null) {
            sentinelResource.setFlowMode(Integer.valueOf(exchange.getRequest().getQueryParams().getFirst("flowMode")));
        }
        if (exchange.getRequest().getQueryParams().getFirst("flowMethod") != null) {
            sentinelResource.setFlowMethod(Integer.valueOf(exchange.getRequest().getQueryParams().getFirst("flowMethod")));
        }
        if (exchange.getRequest().getQueryParams().getFirst("maxQueueTime") != null) {
            sentinelResource.setMaxQueueTime(Integer.valueOf(exchange.getRequest().getQueryParams().getFirst("maxQueueTime")));
        }
        return sentinelResource;
    }



    @GetMapping("removeRule")
    public Mono<TxnRespResult> removeRule(ServerWebExchange exchange) {
        String resourceId = exchange.getRequest().getQueryParams().getFirst("resource");
        String type = exchange.getRequest().getQueryParams().getFirst("type");
        sentinelService.removeRule(resourceId, type);
        return Mono.just(new TxnRespResult().getSuccess(null));
    }

    @GetMapping("getRules")
    public String getRules(ServerWebExchange exchange) {
        String type = exchange.getRequest().getQueryParams().getFirst("type");
        String host = exchange.getRequest().getURI().getHost();
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://" + host + ":" + TransportConfig.getPort() + "/getRules?type=" + type, String.class);
        return result;
    }


    @GetMapping("getLogFile")
    public Mono<String> getLogFile() {
        String baseDir = LogBase.getLogBaseDir();
        String appName = SentinelConfig.getAppName();
        int pid = PidUtil.getPid();
        String logFileName = appName + "-" + pid + MetricWriter.METRIC_FILE_SUFFIX;
        return Mono.just(baseDir + logFileName);
    }

    @GetMapping("getBlockLogFile")
    public Mono<String> getBlockLogFile() {
        String baseDir = LogBase.getLogBaseDir();
        String logFileName = "sentinel-block.log";
        return Mono.just(baseDir + logFileName);
    }

    @GetMapping("getLog")
    public List<SentinelLog> getLog(ServerWebExchange exchange) {
        String host = exchange.getRequest().getURI().getHost();
        String startTime = exchange.getRequest().getQueryParams().getFirst("startTime");
        String endTime = exchange.getRequest().getQueryParams().getFirst("endTime");
        String resourceId = exchange.getRequest().getQueryParams().getFirst("resource");
        List<SentinelLog> logList = getLogDetail(host, startTime, endTime, resourceId);
        logList = collectList(logList);
        return logList;
    }

    @GetMapping("getLogDetail")
    public List<SentinelLog> getLogDetail(ServerWebExchange exchange) {
        String host = exchange.getRequest().getURI().getHost();
        String startTime = exchange.getRequest().getQueryParams().getFirst("startTime");
        String endTime = exchange.getRequest().getQueryParams().getFirst("endTime");
        String resourceId = exchange.getRequest().getQueryParams().getFirst("resource");
        return getLogDetail(host, startTime, endTime, resourceId);
    }

    private List<SentinelLog> getLogDetail(String host, String startTime, String endTime, String resourceId) {
        if (startTime == null || !Pattern.matches("[0-9]{13}", startTime)) {
            startTime = String.valueOf(System.currentTimeMillis() - 60000);
        }
        if (endTime == null || !Pattern.matches("[0-9]{13}", endTime)) {
            endTime = String.valueOf(Long.valueOf(startTime) + 60000);
        }
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + host + ":" + TransportConfig.getPort() + "/metric?startTime=" + startTime + "&endTime=" + endTime;
        if (!StringUtils.isEmpty(resourceId)) {
            url += "&identity=" + resourceId;
        }
        String result = restTemplate.getForObject( url, String.class);
        if (result == null || "No metrics".equals(result)) {
            return null;
        }

        List<SentinelLog> logList = Arrays.stream(result.split("\n")).map(r -> {
            String[] log = r.split("\\|");
            return new SentinelLog(formatDate(Long.valueOf(log[0])), log[1], log[2], log[3], log[4], log[5], log[6]);
        }).collect(Collectors.toList());
        return logList;
    }

    private List<SentinelLog> collectList(List<SentinelLog> list) {
        if (list == null) {
            return null;
        }
        List<SentinelLog> tmpList = new ArrayList();
        list.stream().collect(Collectors.groupingBy(SentinelLog::getResource)).forEach((k, v) -> {
                    Optional<SentinelLog> op = v.stream().reduce((v1, v2) -> {
                        v1.setPassQps(String.valueOf(Integer.valueOf(v1.getPassQps()) + Integer.valueOf(v2.getPassQps())));
                        v1.setBlockQps(String.valueOf(Integer.valueOf(v1.getBlockQps()) + Integer.valueOf(v2.getBlockQps())));
                        v1.setFinishQps(String.valueOf(Integer.valueOf(v1.getFinishQps()) + Integer.valueOf(v2.getFinishQps())));
                        tmpList.add(v1);
                        return v1;
                    });
                    tmpList.add(op.orElse(new SentinelLog()));
                }
        );
        return tmpList.stream().distinct().collect(Collectors.toList());
    }


    private String formatDate(long datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(datetime));
    }

}
