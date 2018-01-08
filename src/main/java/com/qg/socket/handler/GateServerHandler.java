package com.qg.socket.handler;

import com.qg.config.constant.GlobalConfig;
import com.qg.global.cache.OnlineCar;
import com.qg.util.FileUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;


import java.io.File;
import java.util.ArrayList;


/**
 * Created by Dell on 2016/2/1.
 */
@Slf4j
public class GateServerHandler extends ChannelInboundHandlerAdapter {

    private  String carId;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (GlobalConfig.PICTURE_PATH == null) {
            throw new RuntimeException("图片路径未初始化");
        }
        carId = OnlineCar.getInstance().put(ctx.channel());

        log.info("client has connect. the carId is >> : {}", carId);
        FileUtil.deleteAllFiles(new File(GlobalConfig.PICTURE_PATH+carId));
        FileUtil.createDir(GlobalConfig.PICTURE_PATH+carId);
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        ctx.fireChannelRead(message);
    }


//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
//                .addListener(ChannelFutureListener.CLOSE);
//    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("client has disconnect. the carId is >> : {}", carId);
        OnlineCar.getInstance().remove(carId);
        FileUtil.deleteAllFiles(new File(GlobalConfig.PICTURE_PATH+carId));
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.error(cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }




}
