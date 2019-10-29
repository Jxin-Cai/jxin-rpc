package com.jxin.rpc.core.feign;

import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.inject.Singleton;

/**
 * 桩(装)的工厂类
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
@Singleton
public interface FeignFactory {
    /**
     * 生成接口的feign客户端
     * @param  sender     消息发送器
     * @param  insterface 接口字节码对象
     * @param  serverMark 服务标识
     * @param  <T>        接口类的泛型
     * @return feign客户端
     */
    <T> T createFeign(Sender sender, Class<T> insterface, ServerMark serverMark);
}
