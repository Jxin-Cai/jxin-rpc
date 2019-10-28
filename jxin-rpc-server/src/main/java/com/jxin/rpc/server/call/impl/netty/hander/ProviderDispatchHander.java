package com.jxin.rpc.server.call.impl.netty.hander;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.exc.RPCExc;
import com.jxin.rpc.core.consts.ProviderEnum;
import com.jxin.rpc.server.hander.ProviderHander;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求执行器的 调度执行器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 22:37
 */
@Slf4j
@ChannelHandler.Sharable
public class ProviderDispatchHander extends SimpleChannelInboundHandler<MsgContext> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgContext msg) throws RPCExc {
        final ProviderHander providerHander = ProviderEnum.getByType(msg.getHeader().getProviderType());
        if(providerHander == null) {
            throw new RPCExc("No handler for request with type: %d!", msg.getHeader().getProviderType());
        }
        final MsgContext msgContext = providerHander.handle(msg);
        if(msgContext == null) {
            log.error("Response is null!");
        }
        ctx.writeAndFlush(msgContext).addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                log.warn("Write response failed!", channelFuture.cause());
                ctx.channel().close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Exception: ", cause);
        super.exceptionCaught(ctx, cause);
        final Channel channel = ctx.channel();
        if(channel.isActive()) ctx.close();
    }
}
