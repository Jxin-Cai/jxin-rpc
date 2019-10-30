package com.jxin.rpc.core.call.impl.netty;

import com.jxin.rpc.core.call.Server;
import com.jxin.rpc.core.call.impl.netty.hander.channel.ProviderDispatchHander;
import com.jxin.rpc.core.call.impl.netty.hander.coder.req.ReqDecoder;
import com.jxin.rpc.core.call.impl.netty.hander.coder.rsp.RspEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务端 netty实现
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 11:01
 */
public class NettyServer implements Server {
    /**事件调度组*/
    private static final EventLoopGroup masterEventGroup = newEventLoopGroup();
    /**事件执行组*/
    private static final EventLoopGroup workEventGroup = newEventLoopGroup();
    /**连接*/
    private Channel channel;

    /**请求执行器的调度执行器*/
    private static final ProviderDispatchHander PROVIDER_DISPATCH_HANDER = new ProviderDispatchHander();
    /**请求反编译器*/
    private static final ReqDecoder REQ_DECODER = new ReqDecoder();
    /**响应编译器*/
    private static final RspEncoder RSP_ENCODER = new RspEncoder();

    /**
     * 启动服务
     * @param  port 端口号
     * @throws InterruptedException 启动服务失败
     * @author 蔡佳新
     */
    @Override
    public void start(int port) throws InterruptedException {
        // 定义流水线
        final ChannelHandler channelHandlerPipeline = newChannelHandlerPipeline();
        // 创建服务启动实例
        final ServerBootstrap serverBootstrap = newBootstrap(channelHandlerPipeline);

        this.channel = serverBootstrap.bind(port)
                                      .sync()
                                      .channel();
    }

    /**
     * 生成新的事件组
     * @return 事件组
     * @author 蔡佳新
     */
    private static EventLoopGroup newEventLoopGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }

    /**
     * 定义流水线
     * @return 连接执行器
     * @author 蔡佳新
     */
    private ChannelHandler newChannelHandlerPipeline() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline()
                        .addLast(REQ_DECODER)
                        .addLast(RSP_ENCODER)
                        .addLast(PROVIDER_DISPATCH_HANDER);
            }
        };
    }

    /**
     * 创建服务启动实例
     * @param  channelHandler   连接执行器
     * @return 服务启动实例
     * @author 蔡佳新
     */
    private ServerBootstrap newBootstrap(ChannelHandler channelHandler) {
        final ServerBootstrap result = new ServerBootstrap();
        result.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
              .group(masterEventGroup, workEventGroup)
              .childHandler(channelHandler)
              .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return result;
    }

    /**
     * 停止服务
     * @author 蔡佳新
     */
    @Override
    public void close() {
        masterEventGroup.shutdownGracefully();
        workEventGroup.shutdownGracefully();

        if (channel != null) {
            channel.close();
        }
    }

}
