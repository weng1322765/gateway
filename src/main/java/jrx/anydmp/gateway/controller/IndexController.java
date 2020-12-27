package jrx.anydmp.gateway.controller;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author zwg
 * @date 2018-09-20 17:55
 **/

@RestController
public class IndexController {



    @RequestMapping(value = "")
    public String index(){
        return "gateway application is running";
    }

    @GetMapping("home")
    public String home() {
        return "gateway";
    }


}
