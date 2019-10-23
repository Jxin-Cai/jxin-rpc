package com.jxin.rpc.core.call.msg.hander;

import com.jxin.rpc.core.call.msg.MsgContext;

/**
 * 请求消息处理器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:07
 */
public interface ReqMsgHander {
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
