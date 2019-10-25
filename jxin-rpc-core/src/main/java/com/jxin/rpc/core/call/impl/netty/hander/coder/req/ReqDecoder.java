package com.jxin.rpc.core.call.impl.netty.hander.coder.req;

import com.jxin.rpc.core.call.impl.netty.hander.coder.AbstractDecoder;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.ReqHeader;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * 请求反编译器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 21:39
 */
public class ReqDecoder extends AbstractDecoder {
    @Override
    protected Header decodeHeader(ByteBuf byteBuf) {
        final int type = byteBuf.readInt();
        final int version = byteBuf.readInt();

        final int requestIdLen = byteBuf.readInt();
        final byte [] requestByteArr = new byte[requestIdLen];
        byteBuf.readBytes(requestByteArr);
        final String requestId = new String(requestByteArr, StandardCharsets.UTF_8);

        return ReqHeader.builder()
                        .type(type)
                        .version(version)
                        .requestId(requestId)
                        .build();
    }
}
