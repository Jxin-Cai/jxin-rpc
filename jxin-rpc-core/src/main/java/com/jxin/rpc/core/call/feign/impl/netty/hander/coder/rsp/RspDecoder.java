package com.jxin.rpc.core.call.feign.impl.netty.hander.coder.rsp;


import com.jxin.rpc.core.call.feign.impl.netty.hander.coder.AbstractDecoder;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * 响应反编译器
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
public class RspDecoder extends AbstractDecoder {
    @Override
    protected Header decodeHeader(ByteBuf byteBuf) {
        final int type = byteBuf.readInt();
        final int version = byteBuf.readInt();
        final int requestId = byteBuf.readInt();
        final int code = byteBuf.readInt();
        final int errMsgLength = byteBuf.readInt();
        final byte [] errMsgBytes = new byte[errMsgLength];
        byteBuf.readBytes(errMsgBytes);
        final String errMsg = new String(errMsgBytes, StandardCharsets.UTF_8);
        return RspHeader.builder()
                        .type(type)
                        .version(version)
                        .requestId(requestId)
                        .code(code)
                        .errMsg(errMsg)
                        .build();
    }
}
