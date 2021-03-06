﻿package client;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.util.Constant;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

@Service("dealClientServer")
public class DealClientServer {
	@Autowired
    private DealClientServerInitializer dealClientServerInitializer;
	
	public Channel channel;
	
	@PostConstruct
	private void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				doConnect(new NioEventLoopGroup());
			}
		}).start();
	}
	
	public void doConnect(EventLoopGroup group) {
		if (channel != null && channel.isActive()) {
            return;
        }

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_SNDBUF, 32 * 1024);
		bootstrap.option(ChannelOption.SO_RCVBUF, 32 * 1024);
		bootstrap.group(group);
		bootstrap.remoteAddress(Constant.TRADE_SERVER_IP, 7878);
		bootstrap.handler(dealClientServerInitializer);
		
		try {
			ChannelFuture future = bootstrap.connect().addListener(new ConnectionListener()).sync();
			channel = future.channel();
			channel.closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
	
	public void send(String msg) {
		if(channel == null) {
			System.out.println("发送消息时，socket连接已关闭，正在重连.");
			start();
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(channel != null && channel.isActive()) {
			//这里是补充结束符的意思
			if(!msg.endsWith("$_")) {
				msg += "$_";
			}
			ByteBuf bufMsg = Unpooled.copiedBuffer(msg.getBytes());
			channel.writeAndFlush(bufMsg);	
		}
	}
	
	/*public static void main(String[] args) {
		bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.group(eventLoopGroup);
		bootstrap.remoteAddress("127.0.0.1", 7878);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();

		        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
		        pipeline.addLast(new DelimiterBasedFrameDecoder(1024 * 20, delimiter));

		        // 字符串解码 和 编码
		        pipeline.addLast(new StringDecoder());

		        // 自己的逻辑Handler
		        pipeline.addLast("handler", new DealClientServerHandler());
		        
		        pipeline.addLast(new IdleStateHandler(40, 50, 100));
			}
		});
		
		try {
			ChannelFuture future = bootstrap.connect().sync();
			channel = future.channel();
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}