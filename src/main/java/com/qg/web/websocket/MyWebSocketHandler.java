package com.qg.web.websocket;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;


import com.qg.config.constant.GlobalConfig;
import com.qg.global.cache.OnlineCar;
import com.qg.util.FileUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import com.google.gson.Gson;

/**
 * Socket处理器
 * 
 * @author LINhunger
 */
@Component
@Slf4j
public class MyWebSocketHandler implements WebSocketHandler {




	@Autowired
	private Gson gson;


	private static ConcurrentHashMap<String, WebSocketSession> onlineSocket = new ConcurrentHashMap<>();


	/**
	 * 建立连接后
	 */
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String uid = (String) session.getAttributes().get("uid");
		log.info("建立连接 >> : {}",uid);
		boolean flag = false;
		flag = FileUtil.deleteDir(new File(GlobalConfig.PICTURE_PATH+uid));
		FileUtil.deleteDir(new File(GlobalConfig.PICTURE_PATH+uid));
		FileUtil.deleteDir(new File(GlobalConfig.PICTURE_PATH+uid));
		log.info("清空遗留图像 >> : {}",flag);
		onlineSocket.put(uid, session);
		log.info("连接Socket通道数 >> : {}",onlineSocket.size());
	}

	/**
	 * 消息处理，在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理
	 */
	public void handleMessage(WebSocketSession session,WebSocketMessage<?> message) throws Exception {
		log.info("接受信息 >> {}", message.getPayload());
		if (message.getPayloadLength() == 0)
			return;
		if (message.getPayload() != null && message.getPayload().toString().startsWith("@")) return;
		Map request;
		try {
			request  = gson.fromJson(message.getPayload().toString(), HashMap.class);
		}catch (Exception e) {
			log.error("json转换异常 >> : {}", session.getId());
			return;
		}
		String carId = (String) request.get("carId");
		String content = (String) request.get("content");
		if (carId == null || content == null) {
			log.error("数据为空异常 >> : {}", session.getId());
			return;
		}
		deliverCommand(carId, content);
	}

	/**
	 * 向channel转发消息
	 * @param carId
	 * @param content
	 */
	private void deliverCommand(String carId, String content) {
		Channel channel = OnlineCar.getInstance().get(carId);
		if (channel == null) {
			return;
		}
		ChannelFuture future =  channel.writeAndFlush(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
		try {
			future.get();
			log.info("转发信息 >> 小车ID ：{}，信息：{}", carId, content);
		} catch (Exception e) {
			log.error("转发信息异常 >> 小车ID ：{}，信息：{}", carId, content);
			e.printStackTrace();
		}
	}




	/**
	 * 消息传输错误处理
	 */
	public void handleTransportError(WebSocketSession session,Throwable exception) throws Exception {
		if (session.isOpen()) {
			try {
				session.close();
			}
			catch (Exception e) {
				log.error("连接关闭 >> : {}", session.getId());
			}
		}
//		exception.printStackTrace();
		Iterator<Entry<String, WebSocketSession>> it = onlineSocket.entrySet().iterator();
		// 移除Socket会话
		while (it.hasNext()) {
			Entry<String, WebSocketSession> entry = it.next();
			if (entry.getValue().getId().equals(session.getId())) {
				onlineSocket.remove(entry.getKey());
				log.info("Socket会话异常移除:用户ID : {}" , entry.getKey());
				break;
			}
		}
	}

	/**
	 * 关闭连接后
	 */
	public void afterConnectionClosed(WebSocketSession session,CloseStatus closeStatus) throws Exception {

		String uid = (String) session.getAttributes().get("uid");
		//关闭并移除相依channel
		Channel channel =OnlineCar.getInstance().get(uid);
		channel.writeAndFlush(Unpooled.EMPTY_BUFFER)
						.addListener(ChannelFutureListener.CLOSE);
		log.info("连接已移除 >> : {}",uid);

		Iterator<Entry<String, WebSocketSession>> it = onlineSocket.entrySet().iterator();
		// 移除Socket会话
		while (it.hasNext()) {
			Entry<String, WebSocketSession> entry = it.next();
			if (entry.getValue().getId().equals(session.getId())) {
				onlineSocket.remove(entry.getKey());
				log.info("Socket会话已经移除:用户ID : {}" , entry.getKey());
				break;
			}
		}
	}

	public boolean supportsPartialMessages() {
		return false;
	}


	/**
	 * 给某个用户发送消息
	 * @param message
	 * @throws IOException
	 */
	public void sendMessageToUser(String uid, TextMessage message)throws IOException {
		WebSocketSession session = onlineSocket.get(uid);
		if (session != null && session.isOpen()) {
			session.sendMessage(message);
		}
	}
}