package com.jxin.rpc.core.call.msg.manage.impl;

import com.jxin.rpc.core.call.msg.manage.AbstractReqManager;
import com.jxin.rpc.core.consts.ReqManagerEnum;

/**
 * 客户端请求管理器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:38
 */

public class ClientReqManager extends AbstractReqManager {

    @Override
    public int type() {
        return ReqManagerEnum.CLIENT_REQ_MANAGER.getType();
    }

    @Override
    protected void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> System.nanoTime() - entry.getValue().getTimestamp() > LOOP_TIME * 1000000000L);
    }
}
