package com.just.netty.start.server;

import cn.hutool.core.lang.Console;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Scanner;


/**
 * @description:
 * @author: lilang
 * @version:
 * @modified By:1170370113@qq.com
 */
public class NettySrv {
    public  void start() {
        int port =8090;
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChildChannelHandler());

            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel sc) throws Exception {
            ChannelPipeline pipeline = sc.pipeline();
            //处理http消息的编解码
            pipeline.addLast(new MsgHandler());
        }
    }


    private class MsgHandler extends ChannelHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String body = new String(bytes,"UTF-8");
            Console.log("对方发来消息---> msg : {}",body);
            Console.log("回复请输入：");
            Scanner scanner = new Scanner(System.in);
            String res = scanner.nextLine();
            ctx.writeAndFlush(Unpooled.copiedBuffer(res.getBytes()));
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }



    public static void main(String[] args) {
        new NettySrv().start();
    }
}
