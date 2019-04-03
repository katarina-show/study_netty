package com.sjw.demo1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 通报时间的服务端
 * 
 */
public class TimeServer {
	public static void main(String[] args) {
		int port=8080; //服务端默认端口
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("The time server is start in port:"+port);
			Socket socket = null;
			while(true){
				socket = server.accept();
				new Thread(new TimeServerHandler(socket)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(server!=null){
				System.out.println("The time server is close.");
				try {
					server.close();
					server = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
