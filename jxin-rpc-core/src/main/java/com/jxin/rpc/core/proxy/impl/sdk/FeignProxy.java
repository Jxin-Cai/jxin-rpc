package com.jxin.rpc.core.proxy.impl.sdk;

import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.proxy.AbstractFeignProxy;
import com.jxin.rpc.core.call.msg.mark.ServerMark;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk自带的动代实现
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
public class FeignProxy extends AbstractFeignProxy implements InvocationHandler{
    /**
     * 获取代理类
     * @param  clazz      类的字节码对象
     * @param  serverMark 服务标识
     * @param  sender     消息发送器
     * @return 代理类实例
     * @author 蔡佳新
     */
    @Override
    public Object getProxy(Class<?> clazz,
                           ServerMark serverMark,
                           Sender sender) {
        super.serverMark = serverMark;
        super.sender = sender;
        return Proxy.newProxyInstance(FeignProxy.class.getClassLoader(),
                                      clazz.getInterfaces(),
                                     this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args){
        return invokeRemote(method, args);
    }
}
