package com.jxin.rpc.core.call.feign.impl.netty;

import com.jxin.rpc.core.call.feign.Feign;
import com.jxin.rpc.core.call.feign.FeignClient;
import com.jxin.rpc.core.call.feign.impl.netty.hander.coder.req.ReqEncoder;
import com.jxin.rpc.core.call.feign.impl.netty.hander.coder.rsp.RspDecoder;
import com.jxin.rpc.core.call.msg.manage.ReqManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

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
        return NettyFeign.builder()
                         .channel(createChannel(address, connectionTimeout))
                         .reqManager(reqManager)
                         .build();
    }

    /**
     * 建立连接
     * @param  address           连接地址
     * @param  connectionTimeout ttl
     * @return 连接
     * @throws InterruptedException  建立连接被中断
     * @throws TimeoutException      建立连接超时
     * @throws IllegalStateException 参数异常
     * @author 蔡佳新
     */
    private synchronized Channel createChannel(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException, IllegalStateException {
        if (address == null) {
            throw new IllegalArgumentException("address must not be null!");
        }
        if (eventGroup == null) {
            eventGroup = createEventGroup();
        }
        if (bootstrap == null){
            ChannelHandler channelHandlerPipeline = createChannelHandler();
            bootstrap = createBootstrap(channelHandlerPipeline, eventGroup);
        }

        final ChannelFuture channelFuture = bootstrap.connect(address);
        if (!channelFuture.await(connectionTimeout)) {
            throw new TimeoutException();
        }
        final Channel channel = channelFuture.channel();
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException();
        }
        channels.add(channel);
        return channel;
    }

    /**
     * 创建事件组
     * @return 事件组
     * @author 蔡佳新
     */
    private EventLoopGroup createEventGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        }
        return new NioEventLoopGroup();
    }

    /**
     * 创建连接执行器
     * @return 连接执行器
     * @author 蔡佳新

     */
    private ChannelHandler createChannelHandler() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline()
                        .addLast(new RspDecoder())
                        .addLast(new ReqEncoder())
                        .addLast(new ResponseInvocation(inFlightRequests));
            }
        };
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
    public void close() {
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
