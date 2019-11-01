package com.jxin.rpc.core.proxy.impl.cglib;

import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.proxy.AbstractFeignProxy;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB动代实现
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
public class FeignProxy extends AbstractFeignProxy implements MethodInterceptor {
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
        // CGLIB增强类对象
        final Enhancer enhancer = new Enhancer();
        // 设置增强类型
        enhancer.setSuperclass(clazz);
        // 定义代理逻辑对象为当前对象，要求当前对象实现MethodInterceptor方法
        enhancer.setCallback(this);
        return enhancer.create();
    }
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) {
        return invokeRemote(method, args);
    }
}
