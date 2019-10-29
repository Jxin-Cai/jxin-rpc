package com.jxin.rpc.core.call;

/**
 *  消息发送器订阅接口
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/25 20:11
 */
public interface SenderSub {
    /**
     * 赋值消息发送器
     * @param sender 消息发送器
     * @author 蔡佳新
     */
    void setSender(Sender sender);
}
