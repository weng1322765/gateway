package jrx.anydmp.gateway.utils;

import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;

public class CookieUtil {

    public static String getCookie(ServerHttpRequest request, String tokenKey) {
        String token = null;
        if (request == null) {
            return token;
        }
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (cookies.getFirst(tokenKey) == null) {
            return token;
        }
        token = cookies.getFirst(tokenKey).getValue();
        return token;
    }

}
