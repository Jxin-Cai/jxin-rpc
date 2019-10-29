package com.jxin.rpc.core.call.msg.manage.impl;

import com.jxin.rpc.core.call.msg.manage.AbstractReqManager;
import com.jxin.rpc.core.call.msg.manage.RspFuture;
import com.jxin.rpc.core.consts.ReqManagerEnum;

/**
 * 客户端请求管理器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:38
 */

public class ClientReqManager extends AbstractReqManager {

    /**
     * 往全局响应实体容器中添加新的响应实体
     * @param  rspFuture 聚合Future类型响应消息实体的响应体
     * @author 蔡佳新
     */
    @Override
    public void put(RspFuture rspFuture) {
        futureMap.put(rspFuture.getRequestId(), rspFuture);
    }

    /**
     * 删除全局响应实体容器中指定 请求id的参数
     * @param  requestId 请求id
     * @return 被剔除的 value,如果请求id不存在则返回null
     * @author 蔡佳新
     */
    @Override
    public RspFuture remove(String requestId) {
        return futureMap.remove(requestId);
    }

    @Override
    public int type() {
        return ReqManagerEnum.CLIENT_REQ_MANAGER.getType();
    }

    @Override
    protected void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> System.nanoTime() - entry.getValue().getTimestamp() > LOOP_TIME * 1000000000L);
    }
}
