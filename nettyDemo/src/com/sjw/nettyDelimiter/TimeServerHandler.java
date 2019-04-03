package com.sjw.nettyDelimiter;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 用于对网络时间进行读写操作，通常我们只需要关注channelRead和exceptionCaught方法。
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

	private int counter;
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body = (String) msg;
		System.out.println("The time server(Thread:"+Thread.currentThread()+") receive order : "+body+". the counter is : "+ ++counter);
		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
		//通过自定义分隔符改变消息体格式
		currentTime += "$_";
		
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		//将待发送的消息放到发送缓存数组中
		ctx.writeAndFlush(resp);
	}
}
