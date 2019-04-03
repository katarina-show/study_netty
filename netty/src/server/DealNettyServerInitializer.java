package server;

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

@Service("dealNettyServerInitializer")
public class DealNettyServerInitializer extends ChannelInitializer<SocketChannel> {

	@Autowired
	private DealNettyServerHandler dealNettyServerHandler;
	
	@Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        //弄一个特别的字符作为结束符
        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
        
        //缓存大小很重要，小了会断线，大一点没问题，只是标记容量可以多大
        pipeline.addLast(new DelimiterBasedFrameDecoder(1024*1024, delimiter));
        
        // 字符串解码 和 编码
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new IdleStateHandler(40, 50, 100));
        // 自己的逻辑Handler
        pipeline.addLast("handler", dealNettyServerHandler);
    }
}