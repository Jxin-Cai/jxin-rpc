package com.jxin.rpc.core.call.feign.impl.netty.coder;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.ReqHeader;
import com.jxin.rpc.core.exc.RPCExc;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 抽象编译器
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
public class AbstractEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(!(o instanceof MsgContext)) {
            throw new RPCExc("Unknown msg type: %s!", o.getClass().getCanonicalName());
        }

        final MsgContext msg = (MsgContext) o;
        byteBuf.writeInt(Integer.BYTES + msg.getHeader().length() + msg.getBody().length);
        encodeHeader(channelHandlerContext, msg.getHeader(), byteBuf);
        byteBuf.writeBytes(msg.getBody());
    }
    protected void encodeHeader(ChannelHandlerContext channelHandlerContext, Header header, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(header.getType());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeInt(header.getRequestId());
    }
}
