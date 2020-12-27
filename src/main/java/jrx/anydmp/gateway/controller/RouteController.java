package jrx.anydmp.gateway.controller;

import com.alibaba.fastjson.JSON;
import jrx.anydmp.gateway.entity.RouteInfo;
import jrx.anydmp.gateway.servie.IRouteService;
import jrx.anytxn.common.data.TxnRespCode;
import jrx.anytxn.common.data.TxnRespResult;
import jrx.anydmp.gateway.dto.RouteInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private IRouteService routeService;


    @PostMapping("addRoute")
    public Mono<TxnRespResult> create(@RequestBody RouteInfo routeInfo) {
        try {
            routeService.addRoute(routeInfo);
        } catch (Exception e) {
            return Mono.just(new TxnRespResult().getFail(TxnRespCode.ERROR.getCode(), e));
        }
        return Mono.just(new TxnRespResult().getSuccess(null));
    }


    @DeleteMapping("/{routeId}")
    public Mono<TxnRespResult> delete(@PathVariable String routeId) {
        try {
            routeService.delRoute(routeId);
            return Mono.just(new TxnRespResult().getSuccess(null));
        } catch (Exception e) {
            return Mono.just(new TxnRespResult().getFail(TxnRespCode.ERROR.getCode(), e));
        }
    }

    @GetMapping("/routes")
    public Mono<String> routes(ServerWebExchange exchange) {
        String host = exchange.getRequest().getURI().getAuthority();
        String url = "http://" + host + "/actuator/gateway/routes";
        return Mono.just(url);
    }

    @GetMapping("/{routeId}")
    public Mono<TxnRespResult> byId(ServerWebExchange exchange, @PathVariable String routeId) {
        String host = exchange.getRequest().getURI().getAuthority();
        String url = "http://" + host + "/actuator/gateway/routes/" + routeId;
        RouteInfoDTO routeInfo = routeService.queryRouteInfoById(url);
        return Mono.just(new TxnRespResult().getSuccess(JSON.toJSONString(routeInfo)));
    }

}
