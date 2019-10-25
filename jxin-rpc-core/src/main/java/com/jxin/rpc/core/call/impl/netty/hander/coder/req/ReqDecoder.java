package com.jxin.rpc.core.call.impl.netty.hander.coder.req;

import com.jxin.rpc.core.call.impl.netty.hander.coder.AbstractDecoder;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.ReqHeader;
import io.netty.buffer.ByteBuf;

/**
 * 请求反编译器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 21:39
 */
public class ReqDecoder extends AbstractDecoder {
    @Override
    protected Header decodeHeader(ByteBuf byteBuf) {
        return ReqHeader.builder()
                        .type(byteBuf.readInt())
                        .version(byteBuf.readInt())
                        .requestId(byteBuf.readInt())
                        .build();
    }
}
