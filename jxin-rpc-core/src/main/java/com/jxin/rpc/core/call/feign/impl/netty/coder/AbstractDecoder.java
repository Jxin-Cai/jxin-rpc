package com.jxin.rpc.core.call.feign.impl.netty.coder;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.ReqHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 抽象反编译器
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
public abstract class AbstractDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (!byteBuf.isReadable(Integer.BYTES)) {
            return;
        }
        byteBuf.markReaderIndex();
        final int length = byteBuf.readInt() - Integer.BYTES;
        // 缓存区数据长度异常
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }

        final Header header = decodeHeader(channelHandlerContext, byteBuf);
        // 获得定长的字节数组用于接收body
        final byte [] body = new byte[length - header.length()];
        byteBuf.readBytes(body);

        // 生成请求消息
        if(header instanceof ReqHeader){
            list.add(MsgContext.builder()
                               .header(header)
                               .body(body)
                               .build());
        }

    }

    /**
     * 反编译出消息头
     * @param  channelHandlerContext 连接执行器上下文
     * @param  byteBuf               消息字节缓存区
     * @return 消息头
     * @author 蔡佳新
     */
    protected abstract Header decodeHeader(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) ;
}
