package com.codebroker.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        System.out.println(msg);
    }


    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelWritabilityChanged(ctx);
    }


}
