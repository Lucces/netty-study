package com.dmc.server;

import com.dmc.Handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by dongmc on 2018/4/25.
 */


public class EchoServer {
    private static Logger logger = LoggerFactory.getLogger(EchoServer.class);

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }


    public void start() {
        final EchoServerHandler serverHandler = new EchoServerHandler();

        EventLoopGroup group = null;
        try {
            //创建Event-loopgroup
             group = new NioEventLoopGroup();

            //创建Server-bootstrap
            ServerBootstrap bootstrap = new ServerBootstrap();

            //指定所使用的NIO传输channel
            ServerBootstrap b = bootstrap.group(group).channel(NioServerSocketChannel.class);

            //使用指定的端口号创建套接字
            ServerBootstrap bb = b.localAddress(new InetSocketAddress(port));

            //添加一个EchoServerHandle到子channel的ChannelPipeline
            bb.childHandler(new ChannelInitializer() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(serverHandler);
                }
            });


            //异步的绑定服务器，调用sync方法阻塞等待直到绑定完成
            ChannelFuture future = bootstrap.bind().sync();

            //获取channel的CloseFuture，并且阻塞当前线程直至执行完成
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("", e);
        } finally {

            if (group != null) {
                try {
                    group.shutdownGracefully().sync();
                } catch (InterruptedException e) {
                    logger.error("", e);
                }
            }
        }


    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage : xxxx");
        }


        int port = Integer.parseInt(args[0]);

        new EchoServer(port).start();

    }


}
