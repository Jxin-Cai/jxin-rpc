package com.jxin.rpc.core.call.impl.netty.hander.coder.rsp;

import com.jxin.rpc.core.call.impl.netty.hander.coder.AbstractEncoder;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.exc.CoderExc;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 响应编译器
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
public class RspEncoder extends AbstractEncoder {
    @Override
    protected void encodeHeader(Header header, ByteBuf byteBuf) throws CoderExc {
        super.encodeHeader(header, byteBuf);
        if(header instanceof RspHeader) {
            final RspHeader rspHeader = (RspHeader) header;
            byteBuf.writeInt(rspHeader.getCode());
            int errMsgLength = header.length() - (Integer.BYTES * 5);
            byteBuf.writeInt(errMsgLength);
            if(StringUtils.isBlank(rspHeader.getErrMsg())){
                return;
            }
            byteBuf.writeBytes(rspHeader.getErrMsg().getBytes(StandardCharsets.UTF_8));
            return;
        }
        throw new CoderExc(String.format("Invalid header getType: %s!", header.getClass().getCanonicalName()));
    }
}
