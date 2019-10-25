package com.jxin.rpc.core.call.msg.header;

import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * 请求头
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:06
 */
public class ReqHeader extends Header{
    @Builder
    public ReqHeader(String requestId, Integer version, Integer type) {
            super(requestId, version, type);
    }
}
