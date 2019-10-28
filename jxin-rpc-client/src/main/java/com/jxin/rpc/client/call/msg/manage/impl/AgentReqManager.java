package com.jxin.rpc.client.call.msg.manage.impl;

import com.jxin.rpc.client.call.msg.manage.AbstractReqManager;
import com.jxin.rpc.client.call.msg.manage.RspFuture;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 客户端请求管理器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:38
 */

public class AgentReqManager extends AbstractReqManager {
    /**背压机制, 最多同时执行100个任务*/
    private final Semaphore semaphore = new Semaphore(100);
    /**
     * 往全局响应实体容器中添加新的响应实体
     * @param  rspFuture 聚合Future类型响应消息实体的响应体
     * @author 蔡佳新
     */
    @Override
    public void put(RspFuture rspFuture) throws InterruptedException, TimeoutException {
        if(semaphore.tryAcquire(LOOP_TIME, TimeUnit.SECONDS)) {
            futureMap.put(rspFuture.getRequestId(), rspFuture);
            return;
        }
        throw new TimeoutException();
    }

    /**
     * 删除全局响应实体容器中指定 请求id的参数
     * @param  requestId 请求id
     * @return 被剔除的 value,如果请求id不存在则返回null
     * @author 蔡佳新
     */
    @Override
    public RspFuture remove(String requestId) {
        final RspFuture remove = futureMap.remove(requestId);
        if(remove != null){
            semaphore.release();
        }
        return remove;
    }

    @Override
    protected void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> {
            if(System.nanoTime() - entry.getValue().getTimestamp() > LOOP_TIME * 1000000000L){
                semaphore.release();
                return true;
            }
            return false;
        });
    }
}
