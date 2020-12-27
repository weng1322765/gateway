package jrx.anydmp.gateway.filter;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 路由规则与限流规则整合专用
 */
@Component
public class SentinelFilter extends AbstractGatewayFilterFactory<SentinelFilter.SentinelConfig> {
    Logger logger = LoggerFactory.getLogger(getClass());

    public SentinelFilter() {
        super(SentinelFilter.SentinelConfig.class);
    }

    @Override
    public GatewayFilter apply(SentinelFilter.SentinelConfig config) {
        return (exchange, chain) -> {
            Entry entry = null;
            try {
                ContextUtil.enter(config.getResource());
                entry = SphU.entry(config.getResource());
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

    public static class SentinelConfig {
        private String resource;

        public SentinelConfig() {

        }

        public String getResource() {
            return this.resource;
        }

        public SentinelFilter.SentinelConfig setResource(String resource) {
            this.resource = resource;
            return this;
        }
    }
}
