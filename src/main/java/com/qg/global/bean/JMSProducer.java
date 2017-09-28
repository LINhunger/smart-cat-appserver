package com.qg.global.bean;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;

/**
 * Created by 小排骨 on 2017/9/27.
 */
public class JMSProducer {

    private static final String USERNAME= ActiveMQConnection.DEFAULT_USER; // 默认的连接用户名
    private static final String PASSWORD=ActiveMQConnection.DEFAULT_PASSWORD; // 默认的连接密码
    private static final String BROKEURL=ActiveMQConnection.DEFAULT_BROKER_URL; // 默认的连接地址
    private static final int SENDNUM=10;//发送的消息数量

    static ConnectionFactory connectionFactory;//连接工厂
    static Connection connection = null; //连接


    static{
        //实例化连接工厂
        connectionFactory=new org.apache.activemq.ActiveMQConnectionFactory(JMSProducer.USERNAME, JMSProducer.PASSWORD, JMSProducer.BROKEURL);

        try {
            connection=connectionFactory.createConnection();// 通过连接工厂获取连接
            connection.start();//启动连接
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
//            if(connection!=null) {
//                try {
//                    connection.close();
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                }
//            }
        }



    }


    public static void sendMessage(String carId, String content) throws JMSException {

         Session session; //会话 接受或者发送消息的线程
         Destination destination;//消息的目的地
         MessageProducer messageProducer;//消息生产者

        session=connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);// 创建Session
        destination=session.createQueue(carId);//创建一个HelloWorld的消息队列
        messageProducer=session.createProducer(destination);//创建消息生产者
        TextMessage message=session.createTextMessage(content);
        messageProducer.send(message);
        session.commit();
    }
}
