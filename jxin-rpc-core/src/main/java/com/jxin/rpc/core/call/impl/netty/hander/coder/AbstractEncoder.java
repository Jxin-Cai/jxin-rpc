package com.jxin.rpc.core.call.impl.netty.hander.coder;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.exc.CoderExc;
import com.jxin.rpc.core.exc.RPCExc;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 抽象编译器
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
public abstract class AbstractEncoder extends MessageToByteEncoder {
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
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(!(o instanceof MsgContext)) {
            throw new RPCExc("Unknown msg getType: %s!", o.getClass().getCanonicalName());
        }

        final MsgContext msg = (MsgContext) o;
        byteBuf.writeInt(Integer.BYTES + msg.getHeader().length() + msg.getBody().length);
        encodeHeader(msg.getHeader(), byteBuf);
        byteBuf.writeBytes(msg.getBody());
    }
    /**
     * 往<code>byteBuf</code>写入的编译消息头
     * @param  byteBuf               消息字节缓存区
     * @author 蔡佳新
     */
    protected void encodeHeader(Header header, ByteBuf byteBuf) throws CoderExc{
        byteBuf.writeInt(header.getType());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeInt(header.getRequestIdLen());
        byteBuf.writeBytes(header.getRequestId().getBytes(StandardCharsets.UTF_8));
    }
}
