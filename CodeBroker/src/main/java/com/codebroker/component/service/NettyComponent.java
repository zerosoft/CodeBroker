package com.codebroker.component.service;

import com.codebroker.component.BaseCoreService;
import com.codebroker.net.netty.NettyServerInitializer;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NETTY网络服务.
 *
 *  @author LongJu
 */
public class NettyComponent extends BaseCoreService {

    private static final int D_PORT = 6699;
    private final int BACKLOG = 1024;
    Logger logger = LoggerFactory.getLogger(NettyComponent.class);
    int bossGroupNum;
    int workerGroupNum;
    int backlog;
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void init(Object object) {
        logger.info("初始化Netty 开始");
        PropertiesWrapper propertiesWrapper = (PropertiesWrapper) object;

        int defaultValue = Runtime.getRuntime().availableProcessors() * 2;

        bossGroupNum = propertiesWrapper.getIntProperty(SystemEnvironment.NETTY_BOSS_GROUP_NUM, defaultValue);
        workerGroupNum = propertiesWrapper.getIntProperty(SystemEnvironment.NETTY_WORKER_GROUP_NUM, defaultValue);
        backlog = propertiesWrapper.getIntProperty(SystemEnvironment.NETTY_BACKLOG, BACKLOG);

        name = propertiesWrapper.getProperty(SystemEnvironment.NETTY_SERVER_NAME, "NETTY_SERVER");
        int port = propertiesWrapper.getIntProperty(SystemEnvironment.TCP_PORT, D_PORT);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                bootstrap = new ServerBootstrap();
                bossGroup = new NioEventLoopGroup(bossGroupNum);
                workerGroup = new NioEventLoopGroup(workerGroupNum);
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, backlog)
                        .option(ChannelOption.SO_REUSEADDR,true)
                        // .option(ChannelOption.TCP_NODELAY,
                        // Boolean.valueOf(true))
                        // .option(ChannelOption.SO_KEEPALIVE,
                        // Boolean.valueOf(true))
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                        .childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                        .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new NettyServerInitializer());
                ChannelFuture f;
                try {
                    f = bootstrap.bind(port).sync();
                    f.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Netty-Start-Thread");
        thread.start();
        logger.info("初始化Netty 线程启动");
        super.setActive();
    }

    @Override
    public void destroy(Object o) {
        super.destroy(o);
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName().toString();
    }

}
