package com.jxin.rpc.core.netty.feign.impl;

import com.jxin.rpc.core.netty.feign.Feign;
import com.jxin.rpc.core.netty.feign.FeignClient;
import com.jxin.rpc.core.netty.msg.manage.ReqManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * 基于netty实现的 feign 客户端
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 17:53
 */
public class NettyClient implements FeignClient {
    /**事件分组*/
    private EventLoopGroup eventGroup;
    /**netty启动器*/
    private Bootstrap bootstrap;
    /**请求管理器*/
    private final ReqManager reqManager = new ReqManager();
    /**连接列表*/
    private List<Channel> channels = new LinkedList<>();
    @Override
    public Feign createFeign(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        return null;
    }


    /**
     * 创建netty启动器
     * @param  channelHandler 连接执行器
     * @param  eventGroup     事件分组
     * @return netty启动器
     * @author 蔡佳新
     */
    private Bootstrap createBootstrap(ChannelHandler channelHandler, EventLoopGroup eventGroup) {
        return new Bootstrap().channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                              .group(eventGroup)
                              .handler(channelHandler)
                              .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }
    @Override
    public void close() throws IOException {
        for (Channel channel : channels) {
            if(channel != null) {
                channel.close();
            }
        }

        if (eventGroup != null) {
            eventGroup.shutdownGracefully();
        }
        reqManager.close();
    }
}
