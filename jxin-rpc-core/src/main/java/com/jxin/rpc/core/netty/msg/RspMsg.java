package com.jxin.rpc.core.netty.msg;

import com.jxin.rpc.core.netty.msg.header.RspHeader;
import lombok.Builder;
import lombok.Data;

/**
 * 响应消息体
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:22
 */
@Data
@Builder
public class RspMsg {
    /**响应头*/
    private final RspHeader header;
    /**消息正文*/
    private final byte[] body;
}
