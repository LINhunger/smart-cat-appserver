package com.qg.web.websocket;

import com.google.gson.Gson;
import com.qg.entity.Command;
import com.qg.global.bean.JMSProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonJsonParser;
import org.springframework.boot.json.JsonSimpleJsonParser;
import org.springframework.core.annotation.Order;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by 小排骨 on 2017/9/13.
 */
@ServerEndpoint("/app/command")
@Component
@Slf4j
public class MyWebSocket {

    private static int onlineCount = 0;
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    private Session session;
    private Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        System.out.println("有新链接加入!当前在线人数为" + getOnlineCount());
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("有一链接关闭!当前在线人数为" + getOnlineCount());
    }


    @OnMessage
    public void onMessage(String command, Session session) throws Exception {
        System.out.println("来自客户端的消息:" + command);

        if (command != null && command.startsWith("@")) return;

        Map map;
        try {
            map = gson.fromJson(command, Map.class);
        } catch (Exception e) {
            System.err.println("格式转化错误");
            return;
        }

        if (map == null) return;

        String carId = (String) map.get("carId");
        String content = (String) map.get("content");

        if (content == null || carId == null) return;
        //发送指令至消息队列
        JMSProducer.sendMessage(carId,content);
        log.info("command >> {}", command);
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return MyWebSocket.onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }


}