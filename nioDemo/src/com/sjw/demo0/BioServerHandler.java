package com.sjw.demo0;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 服务端线程处理类
 * 
 */
public class BioServerHandler implements Runnable {

	private Socket socket;
	public BioServerHandler(Socket socket){
		this.socket = socket;
	}
	
	@Override
	public void run() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String body = null;
			while(true){
				body = in.readLine(); //阻塞等待数据可以被读取
				if(body == null){
					break;
				}
				System.out.println("服务器接收到指令:"+body);
			}
		} catch (Exception e) {
			if(in != null){
				try {
					in.close();
					in = null;//
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.socket = null;
			}
		}
	}

}
