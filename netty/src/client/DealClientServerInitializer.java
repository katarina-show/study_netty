﻿package client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

@Service("dealClientServerInitializer")
public class DealClientServerInitializer extends ChannelInitializer<SocketChannel> {

	@Autowired
	private DealClientServerHandler dealClientServerHandler;
	
	@Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
        pipeline.addLast(new DelimiterBasedFrameDecoder(1024*1024, delimiter));
            
        // 字符串解码 和 编码
        pipeline.addLast(new StringDecoder());

        pipeline.addLast(new IdleStateHandler(40, 50, 100));
        // 自己的逻辑Handler
        pipeline.addLast("handler", dealClientServerHandler);
    }
}