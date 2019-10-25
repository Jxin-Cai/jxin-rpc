package com.jxin.rpc.core.call.impl.netty.hander.coder.req;

import com.jxin.rpc.core.call.impl.netty.hander.coder.AbstractEncoder;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.exc.CoderExc;
import io.netty.buffer.ByteBuf;

/**
 * 请求编译器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 21:39
 */
public class ReqEncoder extends AbstractEncoder {
    @Override
    protected void encodeHeader(Header header, ByteBuf byteBuf) throws CoderExc {
        encodeHeader(header, byteBuf);
    }
}
