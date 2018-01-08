package com.qg.web.controller;



import com.qg.config.constant.GlobalConfig;
import com.qg.entity.Command;

import com.qg.global.cache.OnlineCar;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小排骨 on 2017/9/7.
 */
@Controller
@Slf4j
public class CommandController {



    @GetMapping("onlineCar")
    @ResponseBody
    public List<String> getOnlineCar(HttpServletRequest session) {
        //一个蹩脚的操作
        if (GlobalConfig.PICTURE_PATH == null) {
            GlobalConfig.PICTURE_PATH = session.getServletContext().getRealPath("/picture/");
            log.info("图片文件路径初始化成功 >> {}", GlobalConfig.PICTURE_PATH);
        }
        List list = OnlineCar.getInstance().keySet();
        return list;

    }
}
