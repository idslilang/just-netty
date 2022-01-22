package com.just.netty.basic.aio;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.socket.aio.AioClient;
import cn.hutool.socket.aio.AioSession;
import cn.hutool.socket.aio.SimpleIoAction;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @description:
 * @author: lilang
 * @version:
 * @modified By:1170370113@qq.com
 */
public class AioCli {

    public void start() {
        AioClient client = new AioClient(new InetSocketAddress("localhost", 8899), new SimpleIoAction() {

            @Override
            public void doAction(AioSession session, ByteBuffer data) {
                if (data.hasRemaining()) {
                    Console.log(StrUtil.utf8Str(data));
                    session.read();
                }
                Console.log("OK");
            }
        });

        client.write(ByteBuffer.wrap("Hello".getBytes()));
        client.read();

        client.close();


    }

    public static void main(String[] args) {
        new AioCli().start();
    }
}
