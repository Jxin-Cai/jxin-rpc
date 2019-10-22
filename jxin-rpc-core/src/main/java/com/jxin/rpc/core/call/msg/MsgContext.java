package com.jxin.rpc.core.call.msg;

import com.jxin.rpc.core.call.msg.header.Header;
import lombok.Builder;
import lombok.Data;

/**
 * 消息上下文
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
@Data
@Builder
public class MsgContext {
    /**消息头*/
    private final Header header;
    /**消息正文*/
    private final byte[] body;
}
