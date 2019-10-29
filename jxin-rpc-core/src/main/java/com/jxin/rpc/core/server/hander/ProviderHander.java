package com.jxin.rpc.core.server.hander;

import com.jxin.rpc.core.call.msg.MsgContext;

/**
 * 请求处理器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:07
 */
public interface ProviderHander {
    /**
     * 处理请求
     * @param  msg 消息
     * @return 响应消息
     */
    MsgContext handle(MsgContext msg);

    /**
     * 请求类型
     */
    int type();
}
