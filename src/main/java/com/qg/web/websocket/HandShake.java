package com.qg.web.websocket;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.qg.config.constant.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * Socket建立连接（握手）和断开
 * 
 * @author LINhunger
 */
@Slf4j
public class HandShake implements HandshakeInterceptor {

	public boolean beforeHandshake(ServerHttpRequest request,ServerHttpResponse response, WebSocketHandler wsHandler,
								   Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			// 标记用户,url“?”后面直接跟要连接的carId
			String connectURI = servletRequest.getURI().toString();
			String uid =connectURI.substring(connectURI.lastIndexOf("?")+1, connectURI.length());
			if (uid != null && !"".equals(uid)) {
				attributes.put("uid", uid);
			} else {
				attributes.put("uid", "o1x0C0TjXP62Yn-mqxhVD-mOVAiY");
			}
		}
		return true;
	}

	public void afterHandshake(ServerHttpRequest request,ServerHttpResponse response, WebSocketHandler wsHandler,
							   Exception exception) {
//		log.info("webscoket 通道开通 >> " );
	}
}