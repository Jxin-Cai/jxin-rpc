package com.jxin.rpc.client.call;

import com.jxin.rpc.core.call.msg.MsgContext;

import java.util.concurrent.CompletableFuture;

/**
 * 消息发送器接口
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 17:19
 */
public interface Sender {
    /**
     * 发送请求命令
     * @param  msg 请求消息体
     * @return Future类型的 响应消息实体
     * @author 蔡佳新
     */
    CompletableFuture<MsgContext> send(MsgContext msg);
}
