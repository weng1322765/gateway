package jrx.anydmp.gateway.config;


import org.springframework.cloud.gateway.filter.factory.AddRequestParameterGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 路由配置类
 *
 * @author zwg
 * @date 2018-09-20 16:24
 **/
@Configuration
public class RouteConfig {


//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("self-anytxn-param", r->r.path("/param/**")
//                    .uri("lb://ANYTXN-PARAM"))
//                .route("anytxn-callcenter", r->r.path("/account/**")
//                        .uri("lb://ANYTXN-CALLCENTER"))
//                .route("anytxn-callcenter-card", r->r.path("/card/**")
//                        .uri("lb://ANYTXN-CALLCENTER"))
//                .route("anytxn-callcenter-customer", r->r.path("/customer/**")
//                        .uri("lb://ANYTXN-CALLCENTER"))
//                .route("anytxn-account",r->r.path("/accounts/**")
//                        .uri("lb://ANYTXN-ACCOUNT"))
//                .route("anytxn-account-opening",r->r.path("/accountopening/**")
//                        .uri("lb://ANYTXN-ACCOUNT"))
//                .route("path_route", r -> r.path("/get")
//                        .uri("http://httpbin.org"))
//                .route("host_route", r -> r.host("*.myhost.org")
//                        .uri("http://httpbin.org"))
//                .route("rewrite_route", r -> r.host("*.rewrite.org")
//                        .filters(f -> f.rewritePath("/foo/(?<segment>.*)",
//                                "/${segment}"))
//                        .uri("http://httpbin.org"))
//                .build();
//    }
}
