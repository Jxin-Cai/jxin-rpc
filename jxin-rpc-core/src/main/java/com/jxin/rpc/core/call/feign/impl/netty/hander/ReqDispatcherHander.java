package com.jxin.rpc.core.call.feign.impl.netty.hander;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.hander.ReqMsgHander;
import com.jxin.rpc.core.consts.ReqEnum;
import com.jxin.rpc.core.exc.RPCExc;
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
public class ReqDispatcherHander extends SimpleChannelInboundHandler<MsgContext> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgContext msg) throws RPCExc {
        final ReqMsgHander reqMsgHander = ReqEnum.getByType(msg.getHeader().getType());
        if(reqMsgHander == null) {
            throw new RPCExc("No handler for request with type: %d!", msg.getHeader().getType());
        }
        final MsgContext msgContext = reqMsgHander.handle(msg);
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
