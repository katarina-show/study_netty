package client;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleStateEvent;

@Service("dealClientServerHandler")
@Scope("prototype")
@Sharable
public class DealClientServerHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		sendHeartbeatPacket(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String rev = (String) msg;
		
		//字符组成由编码，类别，内容等几部分，可以自己划分
		if(rev.startsWith("...")) {
		} else {
	    	if("pong".equals(rev)) {    		
	    		System.out.println("Server Say : " + rev);
	    		return;
	    	}
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接断开，正在重连");
        //获取之前传入的eventLoop，这里面封装了线程池，我们复用它，避免浪费
        final EventLoop eventLoop = ctx.channel().eventLoop();

		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		final DealClientServer client = context.getBean(DealClientServer.class);
		
        //为了不阻塞当前线程，我们使用线程池中的线程来发起重连
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                //调用client的创建连接方法
            	client.doConnect(eventLoop);
            }
        }, 10, TimeUnit.SECONDS);//2秒后发起重连
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 不管是读事件空闲还是写事件空闲都向服务器发送心跳包
            sendHeartbeatPacket(ctx);
        }
    }
    
    /**
     * 发送心跳包
     *
     * @param ctx
     */
    private void sendHeartbeatPacket(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("ping$_".getBytes()));
    }
}