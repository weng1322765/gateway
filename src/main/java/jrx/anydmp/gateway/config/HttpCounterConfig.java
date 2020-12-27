package jrx.anydmp.gateway.config;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import java.util.regex.Pattern;

//@Configuration
public class HttpCounterConfig {
//    private static final Counter counter = Counter.builder("httpCounter").tag("httpCounter", "httpCounter")
//            .description("http请求统计").register(Metrics.globalRegistry);
//    private static final Counter successCounter = Counter.builder("httpSuccessCounter").tag("httpSuccessCounter", "httpSuccessCounter")
//            .description("http请求成功统计").register(Metrics.globalRegistry);
//    private static final Counter failedCounter = Counter.builder("httpFailedCounter").tag("httpFailedCounter", "httpFailedCounter")
//            .description("http请求失败统计").register(Metrics.globalRegistry);
//    private static final Counter blockedCounter = Counter.builder("blockedCounter").tag("blockedCounter", "blockedCounter")
//            .description("http被拦截请求").register(Metrics.globalRegistry);
//
//
//    private static final AtomicInteger atomicInteger = new AtomicInteger();
//    private static final Gauge gauge = Gauge.builder("httpGauge", atomicInteger, AtomicInteger::get).tag("httpGauge", "httpGauge")
//            .description("每秒http").register(Metrics.globalRegistry);
//    private static final Timer timer = new Timer();
//    static {
//        timer.schedule(new OneTask(), 0, 1000);
//    }
//    private static class OneTask extends TimerTask {
//        @Override
//        public void run() {
//            atomicInteger.set(0);
//        }
//    }


//    @Bean
    public WebFilter httpCounterFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            //不统计监控url
            boolean flag = Pattern.matches("/actuator.*", exchange.getRequest().getPath().pathWithinApplication().value());
            if (flag) {
                return chain.filter(exchange);
            }
//            counter.increment();
//            atomicInteger.addAndGet(1);
            return chain.filter(exchange);
//                    .doFinally(r -> {
//                try {
//                    DefaultHttpResponse nettyResponse = ResponseUtil.getDefaultHttpResponse(exchange.getResponse());
//                    if (nettyResponse.status().code() == HttpResponseStatus.OK.code()) {
//                        successCounter.increment();
//                    } else if(nettyResponse.status().code() == HttpResponseStatus.TOO_MANY_REQUESTS.code()){
//                        blockedCounter.increment();
//                    }else{
//                        failedCounter.increment();
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            });
        };
    }


}
