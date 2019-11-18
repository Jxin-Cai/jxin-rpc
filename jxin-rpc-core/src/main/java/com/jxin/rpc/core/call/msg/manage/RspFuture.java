package com.jxin.rpc.core.call.msg.manage;

import com.jxin.rpc.core.call.msg.MsgContext;
import io.netty.util.concurrent.DefaultPromise;
import lombok.Builder;
import lombok.Data;

/**
 * 聚合Future类型响应消息实体的响应体
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:39
 */
@Data
@Builder
public class RspFuture extends DefaultPromise<MsgContext>{
    /**请求id*/
    private final String requestId;
    /**时间截*/
    private final long timestamp;
}
