package com.just.netty.start.client;

import cn.hutool.core.lang.Console;
import com.just.netty.start.encoder.FixedLengthFrameEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Scanner;

/**
 * @description:
 * @author: lilang
 * @version:
 * @modified By:1170370113@qq.com
 */
public class NettyCli {

    public void connect(String host, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 对服务端发送的消息进行粘包和拆包处理，由于服务端发送的消息已经进行了空格补全，
                            // 并且长度为20，因而这里指定的长度也为20
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(1024));
                            // 将粘包和拆包处理得到的消息转换为字符串
                            ch.pipeline().addLast(new StringDecoder());
                            // 对客户端发送的消息进行空格补全，保证其长度为20
                            ch.pipeline().addLast(new FixedLengthFrameEncoder(1024));
                            // 客户端发送消息给服务端，并且处理服务端响应的消息
                            ch.pipeline().addLast(new MsgHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private class MsgHandler extends ChannelHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            Console.log( msg);
            Console.log("请输入回复消息：");
            String res = new Scanner(System.in).nextLine();
            ctx.writeAndFlush("client:" + res);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Console.log("连接上对方：^_^");
            String res = "hello：^_^";
            ctx.writeAndFlush(res);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }

    public static void main(String[] args) {
        try {
            new NettyCli().connect("127.0.0.1", 8090);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
