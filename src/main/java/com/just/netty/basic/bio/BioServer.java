package com.just.netty.basic.bio;

import cn.hutool.core.thread.ExecutorBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @description:
 * @author: lilang
 * @version:
 * @modified By:1170370113@qq.com
 */
public class BioServer {

    public static void main(String[] args) {

      startBio();
    }

    //阻塞IO
    public static void startBio(){
        int port = 8090;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("server start in port " + port);
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当服务端处理速度比较慢的时候，并发连接将会导致大量超时
     */
    public static void  startAbio(){
        ExecutorService executor = ExecutorBuilder.create()//
                .setCorePoolSize(5)//
                .setMaxPoolSize(10)//
                .setKeepAliveTime(10)//
                .build();
        int port = 8091;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("server start in port " + port);
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                executor.submit(new TimeServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            executor.shutdown();
        }
    }

}
