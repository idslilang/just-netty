package com.just.netty.basic.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @description:
 * @author: lilang
 * @version:
 * @modified By:1170370113@qq.com
 */
public class TimeServerClient2 {
    public static void main(String[] args) {
        int port = 8090;
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        Scanner scanner = null;
        try {
            socket = new Socket("127.0.0.1", port);
            //socket 中获取数据
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            out.println("time client start");
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("accept from server:" + line);
                    System.out.println("请输入你想发送的话语：");
                    out.println(scanner.nextLine());
                }
            } catch (IOException e) {
                // ... handle IO exception
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (scanner != null) {
                scanner.close();
            }
        }

    }
}
