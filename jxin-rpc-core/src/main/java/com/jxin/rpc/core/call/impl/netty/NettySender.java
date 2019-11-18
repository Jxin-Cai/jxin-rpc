package com.jxin.rpc.core.call.impl.netty;

import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.manage.ReqManager;
import com.jxin.rpc.core.call.msg.manage.RspFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Builder;

/**
 * 基于netty的 消息发送器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 17:24
 */
@Builder
public class NettySender implements Sender {
    /**连接*/
    private final Channel channel;
    /**请求管理器*/
    private final ReqManager reqManager;
    @Override
    public RspFuture send(MsgContext msg) {
        // 构建返回值
        final RspFuture msgContextFuture = RspFuture.builder()
                                                    .requestId(msg.getHeader().getRequestId())
                                                    .timestamp(System.nanoTime())
                                                    .build();
        try {
            // 将在途请求放到请求管理器中
            reqManager.put(msgContextFuture);
            // 发送命令
            channel.writeAndFlush(msg).addListener((ChannelFutureListener) channelFuture -> {
                // 处理发送失败的情况
                if (!channelFuture.isSuccess()) {
                    msgContextFuture.setFailure(channelFuture.cause());
                    channel.close();
                }
            });
        } catch (Exception e) {
            // 处理发送异常
            reqManager.remove(msg.getHeader().getRequestId());
            msgContextFuture.setFailure(e);
        }
        return msgContextFuture;
    }
}
