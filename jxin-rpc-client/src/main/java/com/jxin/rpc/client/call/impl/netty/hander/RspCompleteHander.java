package com.jxin.rpc.client.call.impl.netty.hander;

import com.jxin.rpc.client.call.msg.manage.ReqManager;
import com.jxin.rpc.client.call.msg.manage.RspFuture;
import com.jxin.rpc.core.call.msg.MsgContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应消息完结执行器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 22:21
 */
@AllArgsConstructor
@Slf4j
@ChannelHandler.Sharable
public class RspCompleteHander extends SimpleChannelInboundHandler<MsgContext> {
    /**请求管理器*/
    private final ReqManager reqManager;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgContext msg) throws Exception {
        final RspFuture rspFuture = reqManager.remove(msg.getHeader().getRequestId());
        if(rspFuture == null){
            log.info("rspFuture is Deleted");
            return;
        }
        rspFuture.getFuture().complete(msg);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive())ctx.close();
    }
}
