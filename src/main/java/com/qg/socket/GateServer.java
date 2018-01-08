package com.qg.socket;


import com.qg.socket.handler.GateServerHandler;
import com.qg.socket.handler.PictureCollectHandler;
import com.qg.util.TransUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;


import java.net.InetSocketAddress;

/**
 * Created by hunger on 2017/8/3.
 */
@Slf4j
public class GateServer {

    public final int port;

    public GateServer(int port) {
        this.port = port;
        startGateServer(port);
        log.info("netty 服务器已启动");
    }

    /**
     * 配置服务器
     * @param port 端口号
     */
    public static void startGateServer(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel)
                            throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        ByteBuf delimiter  = Unpooled.copiedBuffer(TransUtil.toByteArray("FFD9"));
                        pipeline.addLast("DelimiterBasedFrameDecoder", new DelimiterBasedFrameDecoder(100*1024, delimiter));
                        pipeline.addLast("ClientMessageHandler", new GateServerHandler());
                        pipeline.addLast("PictureCollectHandler", new PictureCollectHandler());
                    }
                });
        //配置option
        bindConnectionOptions(bootstrap);
        //绑定端口
        bootstrap.bind(new InetSocketAddress(port)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future)
                    throws Exception {
                if (future.isSuccess()) {
                    log.info("[GateServer] Started Successed, registry is complete, waiting for client connect...");
                } else {
                    log.error("[GateServer] Started Failed, registry is incomplete");
                }
            }
        });
    }

    protected static void bindConnectionOptions(ServerBootstrap bootstrap) {

        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.childOption(ChannelOption.SO_LINGER, 0);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); //心跳机制暂时使用TCP选项

    }

}
