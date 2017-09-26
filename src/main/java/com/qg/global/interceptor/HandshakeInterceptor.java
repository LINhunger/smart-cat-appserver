package com.qg.global.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * Created by 小排骨 on 2017/9/7.
 */
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {



    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("握手前"+request.getURI());
        //http协议转换websoket协议进行前，通常这个拦截器可以用来判断用户合法性等
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (true) {
            return super.beforeHandshake(request, response, wsHandler, attributes);
        }else {
            System.out.println("用户未登录，握手失败！");
            return false;
        }

    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        //握手成功后，通常用来注册用户信息
        System.out.println("握手后");
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
