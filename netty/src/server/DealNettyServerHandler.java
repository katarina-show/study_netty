﻿package server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ruizton.main.model.FentrustVcoin;
import com.ruizton.main.websocket.controller.WsMarketController;
import com.ruizton.util.ConstantKeys;
import com.ruizton.util.DealMakingVcoinUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("dealNettyServerHandler")
@Scope("prototype")
//特别注意这个注解@Sharable，默认的4版本不能自动导入匹配的包，需要手动加入
//地址是import io.netty.channel.ChannelHandler.Sharable;
@Sharable
public class DealNettyServerHandler extends ChannelInboundHandlerAdapter {
	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel incoming = ctx.channel();
        channels.add(incoming);

        System.out.println("[SERVER] - " + incoming.remoteAddress() + " 加入\n");
    } 
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
        Channel incoming = ctx.channel();
        
        System.out.println("[SERVER] - " + incoming.remoteAddress() + " 离开\n");
        channels.remove(incoming);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	String rev = (String) msg;
		
    	//业务代码判断，可在字符串中做一些限制，比如字符串由编码，类别，内容组成，方便区分判断
		if(rev.startsWith("")) {
		} else {
	    	if("ping".equals(rev)) {    		
	    		ctx.writeAndFlush(Unpooled.copiedBuffer("pong$_".getBytes()));
	    		return;
	    	}
		}
    }
    
    /*
     * 
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     * 
     * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}