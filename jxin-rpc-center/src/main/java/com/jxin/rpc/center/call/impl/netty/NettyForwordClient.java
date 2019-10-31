package com.jxin.rpc.center.call.impl.netty;

import com.jxin.rpc.center.call.ForwordClient;
import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.impl.netty.NettySender;
import com.jxin.rpc.core.call.impl.netty.hander.channel.RspCompleteHander;
import com.jxin.rpc.core.call.impl.netty.hander.coder.req.ReqEncoder;
import com.jxin.rpc.core.call.impl.netty.hander.coder.rsp.RspDecoder;
import com.jxin.rpc.core.call.msg.manage.ReqManager;
import com.jxin.rpc.core.call.msg.manage.impl.ClientReqManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 *  请求转发客户端 netty实现
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 17:22
 */
public class NettyForwordClient implements ForwordClient {
    /**事件分组*/
    private static final EventLoopGroup EVENT_GROUP = createEventGroup();
    /**netty启动器*/
    private static final Bootstrap BOOTSTRAP = createBootstrap();
    /**请求管理器*/
    private static final ReqManager REQ_MANAGER = new ClientReqManager();
    /**连接列表*/
    private static final List<Channel> CHANNEL_LIST = new LinkedList<>();

    /**响应消息完结执行器*/
    private static final RspCompleteHander RSP_COMPLETE_HANDER;
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
        assert address != null : "non null address required!";

        final ChannelFuture channelFuture = BOOTSTRAP.connect(address);
        if (!channelFuture.await(connectionTimeout)) {
            throw new TimeoutException();
        }
        final Channel channel = channelFuture.channel();
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException();
        }
        CHANNEL_LIST.add(channel);
        return channel;
    }

    /**
     * 创建事件组
     * @return 事件组
     * @author 蔡佳新
     */
    private static EventLoopGroup createEventGroup() {
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
    private static ChannelHandler createChannelHandler() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline()
                       .addLast(new RspDecoder())
                       .addLast(new ReqEncoder())
                       .addLast(RSP_COMPLETE_HANDER);
            }
        };
    }
    /**
     * 创建netty启动器
     * @return netty启动器
     * @author 蔡佳新
     */
    private static Bootstrap createBootstrap() {
        return new Bootstrap().channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                              .group(EVENT_GROUP)
                              .handler(createChannelHandler())
                              .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }
    @Override
    public void close() throws IOException {
        for (Channel channel : CHANNEL_LIST) {
            if(channel != null) {
                channel.close();
            }
        }
        EVENT_GROUP.shutdownGracefully();
        REQ_MANAGER.close();
    }
}
