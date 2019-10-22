package com.jxin.rpc.core.call.feign.impl.netty.hander.coder;

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
    /**
     * byteBuf的组成 :
     *  req:
     *  1.整个消息体占用的字节数        4字节
     *  2.类型占用的字节数              4字节
     *  3.版本占用的字节数              4字节
     *  4.请求Id占用的字节数            4字节
     *  rsq:
     *  1.整个消息体占用的字节数        4字节
     *  2.类型占用的字节数              4字节
     *  3.版本占用的字节数              4字节
     *  4.请求Id占用的字节数            4字节
     *  5.返回code占用的字节数          4字节
     *  6.消息占用字节数的值占用的字节数 4字节
     *  7.消息占用的字节数              errMsg.getBytes().length
     */
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

        final Header header = decodeHeader(byteBuf);
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
     * 从<code>byteBuf</code>反编译出消息头
     * @param  byteBuf               消息字节缓存区
     * @return 消息头
     * @author 蔡佳新
     */
    protected abstract Header decodeHeader(ByteBuf byteBuf) ;
}
