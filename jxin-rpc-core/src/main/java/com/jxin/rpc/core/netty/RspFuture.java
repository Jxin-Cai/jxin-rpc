package com.jxin.rpc.core.netty;

import com.jxin.rpc.core.netty.msg.RspMsg;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

/**
 * 未来响应实体
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:39
 */
@Data
@Builder
public class RspFuture {
    private final int requestId;
    private final CompletableFuture<RspMsg> future;
    private final long timestamp;
}
