package com.jxin.rpc.client.call.msg.manage;

import com.jxin.rpc.core.inject.Singleton;

import java.io.Closeable;
import java.util.concurrent.TimeoutException;

/**
 * 请求管理器接口
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/28 15:46
 */
@Singleton
public interface ReqManager extends Closeable {
    /**
     * 往全局响应实体容器中添加新的响应实体
     * @param  rspFuture 聚合Future类型响应消息实体的响应体
     * @author 蔡佳新
     */
    void put(RspFuture rspFuture) throws InterruptedException, TimeoutException;


    /**
     * 删除全局响应实体容器中指定 请求id的参数
     * @param  requestId 请求id
     * @return 被剔除的 value,如果请求id不存在则返回null
     * @author 蔡佳新
     */
    RspFuture remove(String requestId);
}
