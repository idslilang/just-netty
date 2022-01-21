package com.just.netty.basic.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @description:
 * @author: lilang
 * @version:
 * @modified By:1170370113@qq.com
 */
public class TimeServerHandler implements Runnable {


    Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));

            out = new PrintWriter(socket.getOutputStream(), true);

            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("time server accept data :" + line);

                    out.println("server has accept your data thank you!");
                }
            } catch (IOException e) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                in = null;
                out.close();
                out = null;
                if (this.socket != null) {
                    this.socket.close();
                    this.socket = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
