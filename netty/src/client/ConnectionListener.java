﻿package client;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import java.util.concurrent.TimeUnit;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.ruizton.main.auto.OneDayDataVcoin;
/**
 *
 * @author xiaojie.zhu
 */
public class ConnectionListener implements ChannelFutureListener {
    /**
     * 这里处理连接失败的情况下重连，比如说启动应用程序时，刚好网络中断，这个时候是不会触发channel中的断开连接事件，
     * 所以需要监听连接失败的情况，只要连接失败，就会不停的进入这里进行重连，所以是无限重连
     * @param future
     * @throws Exception
     */
    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if(!future.isSuccess()){
            System.out.println("连接失败，正在重新连接");

    		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    		final DealClientServer client = context.getBean(DealClientServer.class);
    		
            //获取之前传入的eventLoop，这里面封装了线程池，我们复用它，避免浪费
            final EventLoop eventLoop = future.channel().eventLoop();
            //为了不阻塞当前线程，我们使用线程池中的线程来发起重连
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                    //调用client的创建连接方法
                	client.doConnect(eventLoop);
                }
            }, 2, TimeUnit.SECONDS);//2秒后发起重连
        }else{
            System.out.println("连接成功");
        }
    }
}
