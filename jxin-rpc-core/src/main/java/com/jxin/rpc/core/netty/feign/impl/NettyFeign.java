package com.jxin.rpc.core.netty.feign.impl;

import com.jxin.rpc.core.netty.feign.Feign;
import com.jxin.rpc.core.netty.msg.ReqMsg;
import com.jxin.rpc.core.netty.msg.RspMsg;
import com.jxin.rpc.core.netty.msg.manage.ReqManager;
import com.jxin.rpc.core.netty.msg.manage.RspFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.AllArgsConstructor;

import java.util.concurrent.CompletableFuture;

/**
 * 基于netty的 发送消息接口 Fegin实现
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 17:24
 */
@AllArgsConstructor
public class NettyFeign implements Feign {
    private final Channel channel;
    private final ReqManager reqManager;

    @Override
    public CompletableFuture<RspMsg> send(ReqMsg reqMsg) {
        // 构建返回值
        final CompletableFuture<RspMsg> rspMsgFuture = new CompletableFuture<>();
        try {
            // 将在途请求放到inFlightRequests中
            reqManager.put(RspFuture.builder()
                                    .requestId(reqMsg.getHeader().getRequestId())
                                    .future(rspMsgFuture)
                                    .timestamp(System.nanoTime())
                                    .build());
            // 发送命令
            channel.writeAndFlush(reqMsg).addListener((ChannelFutureListener) channelFuture -> {
                // 处理发送失败的情况
                if (!channelFuture.isSuccess()) {
                    rspMsgFuture.completeExceptionally(channelFuture.cause());
                    channel.close();
                }
            });
        } catch (Exception e) {
            // 处理发送异常
            reqManager.remove(reqMsg.getHeader().getRequestId());
            rspMsgFuture.completeExceptionally(e);
        }
        return rspMsgFuture;
    }
}
