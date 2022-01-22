package com.just.netty.start.server;

import cn.hutool.core.lang.Console;
import com.just.netty.start.encoder.FixedLengthFrameEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Scanner;


/**
 * @description:
 * @author: lilang
 * @version:
 * @modified By:1170370113@qq.com
 */
public class NettySrv {
    public void start(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 这里将FixedLengthFrameDecoder添加到pipeline中，指定长度为20
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(1024));
                            // 将前一步解码得到的数据转码为字符串
                            ch.pipeline().addLast(new StringDecoder());
                            // 这里FixedLengthFrameEncoder是我们自定义的，用于将长度不足20的消息进行补全空格
                            ch.pipeline().addLast(new FixedLengthFrameEncoder(1024));
                            // 最终的数据处理
                            ch.pipeline().addLast(new MsgHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    private class MsgHandler extends ChannelHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Console.log(msg);
            Scanner scanner = new Scanner(System.in);
            Console.log("请输入回复消息：");
            ctx.writeAndFlush("server msg:"+scanner.nextLine());
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }



    public static void main(String[] args) {
        try {
            new NettySrv().start(8090);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
