package com.jxin.rpc.core.call.feign.impl.netty;

import com.jxin.rpc.core.call.feign.Sender;
import com.jxin.rpc.core.call.feign.Client;
import com.jxin.rpc.core.call.feign.impl.netty.hander.RspCompleteHander;
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
 * 基于netty实现的 客户端
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 17:53
 */
public class NettyClient implements Client {
    /**事件分组*/
    private EventLoopGroup eventGroup;
    /**netty启动器*/
    private Bootstrap bootstrap;
    /**请求管理器*/
    private static final ReqManager REQ_MANAGER = new ReqManager();
    /**连接列表*/
    private List<Channel> channels = new LinkedList<>();

    /**响应消息完结执行器*/
    private static final RspCompleteHander RSP_COMPLETE_HANDER;
    /**响应反编译器*/
    private static final RspDecoder RSP_DECODER = new RspDecoder();
    /**请求编译器*/
    private static final ReqEncoder REQ_ENCODER = new ReqEncoder();
    static {
        RSP_COMPLETE_HANDER = new RspCompleteHander(REQ_MANAGER);
    }
    /**
     * 生成发送消息的 消息发送器
     * @param  address           套接字地址
     * @param  connectionTimeout 连接超时时间
     * @return 发送消息的 Fegin接口
     * @throws InterruptedException  建立连接被中断
     * @throws TimeoutException      建立连接超时
     * @throws IllegalStateException 参数异常
     * @author 蔡佳新
     */
    @Override
    public Sender createSender(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        return NettySender.builder()
                          .channel(createChannel(address, connectionTimeout))
                          .reqManager(REQ_MANAGER)
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
    private synchronized Channel createChannel(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        if (address == null) {
            throw new IllegalArgumentException("non null address required!");
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
                        .addLast(RSP_DECODER)
                        .addLast(REQ_ENCODER)
                        .addLast(RSP_COMPLETE_HANDER);
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
        REQ_MANAGER.close();
    }
}
