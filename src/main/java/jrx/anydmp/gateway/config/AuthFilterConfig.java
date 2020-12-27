package jrx.anydmp.gateway.config;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import jrx.anydmp.gateway.common.constant.AuthConstants;
import jrx.anydmp.gateway.common.enums.AuthValidStatus;
import jrx.anydmp.gateway.common.enums.UrlType;
import jrx.anydmp.gateway.common.exceptions.AuthException;
import jrx.anydmp.gateway.config.settings.FilterSettings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

/**
 * 鉴权
 */
@Configuration
public class AuthFilterConfig {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    FilterSettings filterSettings;

    @Bean
    public WebFilter authFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {

            if (filterSettings.getAuthModel() == 0) {
                return chain.filter(exchange);
            }

            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().pathWithinApplication().value();
            if(getUrlType(path).equals(UrlType.Pass)) {
                return chain.filter(exchange);
            }

            String token = null;
//            不做登录，只做鉴权，暂时不从cookie获取token
//            try {
//                token = CookieUtil.getCookie(request, AuthConstants.TOKEN);
//            } catch (Exception e) {
//                logger.error("get token is error:" + e.getMessage(), e);
//            }

            //第三方请求服务时，从header获取token
            if (StringUtils.isEmpty(token)) {
                String header = request.getHeaders().getFirst(AuthConstants.AUTH_KEY);
                if (StringUtils.isNotEmpty(header)) {
                    token = header.replaceAll(AuthConstants.AUTH_PRO, "");
                }
            }

            if(StringUtils.isEmpty(token)){
                throw new AuthException("没有权限！");
            }

            sendAuth(request, path, token);

            return chain.filter(exchange);
        };
    }


    /**
     * 获取此url类型，进行下一步操作
     * @param url
     */
    private UrlType getUrlType(String url){

        List<String> allows = filterSettings.getAllows();

        AntPathMatcher antPathMatcher = new AntPathMatcher();

        //先判断index，index可能是html
        for (String a : allows) {
            if (antPathMatcher.match(a, url)) {
                return UrlType.Pass;
            }
        }

        if (antPathMatcher.match(AuthConstants.LOGIN_URL, url)) {
            return UrlType.Login;
        }

        return UrlType.NeedAuth;
    }


    /**
     * 鉴权
     */
    private void sendAuth(ServerHttpRequest request, String url, String token) {

        RestTemplate restTemplate = new RestTemplate();
        RequestEntity requestEntity = getHttpEntity(request, token, url);
        if (requestEntity == null) {
            throw new AuthException("鉴权异常！请检查配置！");
        }
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        String data = responseEntity.getBody();

        if (StringUtils.isEmpty(data)) {
            logger.error("--------------get auth error, result is null----------------");
            throw new AuthException("请求鉴权返回为空！");
        }
        logger.debug("--------------auth return data：{}", data);
        JSONObject responseJ = JSON.parseObject(data);
        String result = responseJ.getString("tokenStatus");
        String msg = null;
        if (result.equals(AuthValidStatus.ACCESS.name())) {
            logger.debug("auth is access");
        } else if (result.equals(AuthValidStatus.DENY.name())) {
            msg = AuthValidStatus.DENY.getName();
        } else if (result.equals(AuthValidStatus.EXPIRED.name())) {
            msg = AuthValidStatus.EXPIRED.getName();
        } else if (result.equals(AuthValidStatus.STOP.name())) {
            msg = AuthValidStatus.STOP.getName();
        } else if (result.equals(AuthValidStatus.MUTILOGIN.name())) {
            msg = AuthValidStatus.MUTILOGIN.getName();
        } else {
            msg = "未知錯誤！";
        }
        if (msg != null) {
            throw new AuthException(msg);
        }
    }

    private RequestEntity getHttpEntity(ServerHttpRequest request, String token, String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(AuthConstants.AUTH_KEY, AuthConstants.AUTH_PRO + token);
            String authUrl = filterSettings.getAuthUrl() + "?url=" +
                    url +
                    "&method=" +
                    request.getMethod() +
                    "&address=" +
                    Base64.getEncoder().encodeToString(filterSettings.getLocalUrl().getBytes(AuthConstants.UTF8));
            return new RequestEntity(headers, HttpMethod.GET, URI.create(authUrl));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

}
