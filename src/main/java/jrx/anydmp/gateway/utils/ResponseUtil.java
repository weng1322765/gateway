package jrx.anydmp.gateway.utils;

import io.netty.handler.codec.http.DefaultHttpResponse;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ResponseUtil {



    /**
     * 反射获取从非pubic类ReactorServerHttpResponse对象获取内部DefaultHttpResponse属性对象
     * @param reactorRepsonse
     * @return
     * @throws IllegalAccessException
     */
    public static DefaultHttpResponse getDefaultHttpResponse(Object reactorRepsonse) throws IllegalAccessException {
        Field responseField = Arrays.stream(reactorRepsonse.getClass()
                .getDeclaredFields()).filter(field ->
                field.getName().equals("response")).findFirst().get();
        responseField.setAccessible(true);
        Object response = responseField.get(reactorRepsonse);

        Field nettyResponseField = Arrays.stream(response.getClass()
                .getDeclaredFields()).filter(field ->
                field.getName().equals("nettyResponse")).findFirst().get();
        nettyResponseField.setAccessible(true);
        return (DefaultHttpResponse) nettyResponseField.get(response);
    }
}
