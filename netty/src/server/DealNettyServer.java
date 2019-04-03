package server;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Service("dealNettyServer")
public class DealNettyServer {
	private static Logger log = Logger.getLogger(DealNettyServer.class);
    //自动装备变量，spring会根据名字或者类型来装备这个变量，注解方式不需要set get方法了
    @Autowired
    private DealNettyServerInitializer dealNettyServerInitializer;

    //程序初始方法入口注解，提示spring这个程序先执行这里
	@PostConstruct
    public void serverStart() throws InterruptedException{
		new Thread(new Runnable() {
			@Override
			public void run() {
				EventLoopGroup bossGroup = new NioEventLoopGroup();
		        EventLoopGroup workerGroup = new NioEventLoopGroup();
		        try {
		            ServerBootstrap b = new ServerBootstrap();
		            b.group(bossGroup, workerGroup);
		            b.channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100);
		            b.childHandler(dealNettyServerInitializer);
		            
		            // 服务器绑定端口监听
		            ChannelFuture f = b.bind(7878).sync();
		            // 监听服务器关闭监听
		            f.channel().closeFuture().sync();
		            
		        } catch (InterruptedException e) {
		        	log.error(e.getMessage(), e);
				} finally {
		            bossGroup.shutdownGracefully();
		            workerGroup.shutdownGracefully();
		        }
			}
		}).start();
    }
}