package com.jxin.rpc.core.netty.msg.manage;

import com.jxin.rpc.core.netty.msg.RspMsg;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

/**
 * 聚合Future类型响应消息实体的响应体
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:39
 */
@Data
@Builder
public class RspFuture {
    /**请求id*/
    private final int requestId;
    /**时间截*/
    private final long timestamp;
    /**Future类型的 响应消息实体*/
    private final CompletableFuture<RspMsg> future;
}
