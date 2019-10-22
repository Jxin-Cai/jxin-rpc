package com.jxin.rpc.core.call.msg.header;

import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * 响应头
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:06
 */
public class RspHeader extends Header{
    /**响应code*/
    private final Integer code;
    /**异常消息*/
    private final String errMsg;
    @Builder
    public RspHeader(int requestId,
                     Integer version,
                     Integer type,
                     Integer code,
                     String errMsg) {
        super(requestId, version, type);
        this.code = code;
        this.errMsg = errMsg;
    }
    /**
     * 获取当前实体bean属性值所占空间之和
     * @return 实体bean所有属性所占的空间
     * @author 蔡佳新
     */
    @Override
    public int length() {
        return Integer.BYTES
               + Integer.BYTES
               + Integer.BYTES
               + Integer.BYTES
               + Integer.BYTES
               + (errMsg == null ? 0 : errMsg.getBytes(StandardCharsets.UTF_8).length);
    }
}
