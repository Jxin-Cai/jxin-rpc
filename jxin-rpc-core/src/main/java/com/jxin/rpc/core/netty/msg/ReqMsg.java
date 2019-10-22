package com.jxin.rpc.core.netty.msg;

import com.jxin.rpc.core.netty.msg.header.ReqHeader;
import lombok.Builder;
import lombok.Data;

/**
 * 请求消息体
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:24
 */
@Data
@Builder
public class ReqMsg {
    /**响应头*/
    private final ReqHeader header;
    /**消息正文*/
    private final byte[] body;
}
