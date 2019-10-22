package com.jxin.rpc.core.netty.msg.manage;

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
    /**请求id*/
    private final int requestId;
    /**未来响应消息实体*/
    private final CompletableFuture<RspMsg> future;
    /**时间截*/
    private final long timestamp;
}
