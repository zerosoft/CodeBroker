package com.codebroker.web.api.service.netty;


import com.codebroker.web.api.service.api.IRegisty;
import com.codebroker.web.api.service.netty.handler.NettyRPCClientHandler;
import com.codebroker.web.api.util.Platform;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.codebroker.net.netty.filter.ByteArrayPacketCodecDecoder;
import com.codebroker.net.netty.filter.ByteArrayPacketCodecEncoder;
/**
 * description
 *
 * @author Dragon
 * @Date 2019/10/24
 */
public class TCPClient {

    private Logger logger = LoggerFactory.getLogger(TCPClient.class);
    public static AttributeKey sessionWithActor=AttributeKey.newInstance("NETTY_SESSION");

    private String hostname;
    private int port;
    private IRegisty registy;

    public void setRegisty(IRegisty registy) {
        this.registy = registy;
    }

    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private FixedChannelPool channelPool;

    public TCPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void stop() throws Exception {
        workerGroup.shutdownGracefully();
        logger.info("netty tcp connect[{}][{}]-stop", hostname, port);
    }
    /**
     * 单通道使用
     * @return
     */
    public Channel connect() {
        try {
            if (Platform.isLinux()) {
                workerGroup = new EpollEventLoopGroup(Platform.availableProcessors() * 2);
            } else {
                workerGroup = new NioEventLoopGroup(Platform.availableProcessors() * 2);
            }

            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);

            if (Platform.isLinux()) {
                bootstrap.channel(EpollSocketChannel.class);
            } else {
                bootstrap.channel(NioSocketChannel.class);
            }

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("MessageHead-decoder", new ByteArrayPacketCodecDecoder());
                    ch.pipeline().addLast("MessageHead-encoder", new ByteArrayPacketCodecEncoder());
                    ch.pipeline().addLast("handler", new NettyRPCClientHandler(registy));
                }
            });

            // 参数设置
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);

            ChannelFuture f = bootstrap.connect(hostname, port).sync();
            f.addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("netty tcp connect[{}][{}] success", hostname, port);
                } else {
                    logger.info("netty tcp connect[{}][{}] fail", hostname, port);
                }
            });

            Channel channel = f.channel();
            f.channel().closeFuture().sync();
            return channel;
        } catch (Exception e) {
            logger.info("netty tcp connect[{}][{}] err", hostname, port);
            e.printStackTrace();
            return null;
        } finally {
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }

    }

    private ChannelPoolHandler channelPoolHandler = new ChannelPoolHandler() {


        @Override
        public void channelReleased(Channel ch) throws Exception {
            // 刷新管道里的数据
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER); // flush掉所有写回的数据
            logger.info("刷新管道里的数据");
        }

        @Override
        public void channelCreated(Channel ch) throws Exception {
            // 客户端逻辑处理 ClientHandler这个也是咱们自己编写的，继承ChannelInboundHandlerAdapter
            ch.pipeline().addLast("ping", new IdleStateHandler(400, 600, 1000, TimeUnit.SECONDS));
            ch.pipeline().addLast("MessageHead-decoder", new ByteArrayPacketCodecDecoder());
            ch.pipeline().addLast("MessageHead-encoder", new ByteArrayPacketCodecEncoder());
            ch.pipeline().addLast("handler", new NettyRPCClientHandler(registy));
            logger.info("客户端逻辑处理 channelCreated="+ch.id());
        }

        @Override
        public void channelAcquired(Channel ch) throws Exception {
            logger.info("客户端逻辑处理 channelAcquired="+ch.id());
        }
    };
    /**
     * 多通道使用
     * @param maxChannel
     */
    public void connect(int maxChannel) {

        if (Platform.isLinux()) {
            workerGroup = new EpollEventLoopGroup(Platform.availableProcessors() * 2);
        } else {
            workerGroup = new NioEventLoopGroup(Platform.availableProcessors() * 2);
        }

        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);

        if (Platform.isLinux()) {
            bootstrap.channel(EpollSocketChannel.class);
        } else {
            bootstrap.channel(NioSocketChannel.class);
        }

        // 连接池每次初始化一个连接的时候都会根据这个值去连接服务器
        InetSocketAddress remoteaddress = InetSocketAddress.createUnresolved(hostname, port);// 连接地址
        bootstrap.option(ChannelOption.TCP_NODELAY, true).remoteAddress(remoteaddress);
        // 初始化连接池
        // 这个值可要好好保管好了，后面拿连接放连接都要通过它啦
        channelPool = new FixedChannelPool(bootstrap, channelPoolHandler, maxChannel);


    }

    public Channel getChannel()  {
        Channel channel = null;
        try {
            if (Objects.nonNull(this.channelPool)){
                channel = this.channelPool.acquire().get();
            }
        } catch (InterruptedException |ExecutionException e) {
            e.printStackTrace();
        }
        // 写出数据
        // channel.write("xxxx");

        // 连接放回连接池,这里一定记得放回去
        // this.channelPool.release(channel);
        return channel;
    }
}
