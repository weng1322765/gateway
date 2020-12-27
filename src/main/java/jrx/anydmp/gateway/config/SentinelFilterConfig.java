package jrx.anydmp.gateway.config;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

/**
 * 根据具体请求url进行限流
 */
@Configuration
public class SentinelFilterConfig {
//    Logger logger = LoggerFactory.getLogger(getClass());


    @Bean
    public WebFilter sentinelWebFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            //不统计监控
            boolean flag = Pattern.matches("/actuator.*", exchange.getRequest().getPath().pathWithinApplication().value());
            if (flag) {
                return chain.filter(exchange);
            }
            Entry entry = null;
            try {
                String path = exchange.getRequest().getPath().pathWithinApplication().value();
                ContextUtil.enter(path);
                entry = SphU.entry(path);
                return chain.filter(exchange);
            } catch (BlockException e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return response.writeWith(Mono.empty());
            } finally {
                if (entry != null) {
                    entry.exit();
                }
                ContextUtil.exit();
            }
        };
    }

 }
