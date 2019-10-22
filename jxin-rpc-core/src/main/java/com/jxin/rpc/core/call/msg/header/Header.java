package com.jxin.rpc.core.call.msg.header;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
@Data
@AllArgsConstructor
public abstract class Header {
    /**请求id*/
    private final int requestId;
    /**协议版本*/
    private final Integer version;
    /**请求类型*/
    private final Integer type;

    public abstract int length();
}
