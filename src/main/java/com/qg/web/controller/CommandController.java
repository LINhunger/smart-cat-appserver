package com.qg.web.controller;


import com.qg.entity.Command;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小排骨 on 2017/9/7.
 */
@Controller
@Slf4j
public class CommandController {



    @Autowired
    private  JmsMessagingTemplate jmsMessagingTemplate;


    @MessageMapping("/command")
    public void receivedCommand(Command command) {
        if (command == null) return;
        long carId = command.getCarId();
        ActiveMQQueue mqQueue = new ActiveMQQueue(""+carId);
        //发送指令至消息队列
        jmsMessagingTemplate.convertAndSend(mqQueue, command.getContent());
        log.info("command >> {}", command);
    }

    @GetMapping("onlineCar")
    public List<String> getOnlineCar() {
        List list = new ArrayList();
        list.add("1");
        return list;
    }
}
