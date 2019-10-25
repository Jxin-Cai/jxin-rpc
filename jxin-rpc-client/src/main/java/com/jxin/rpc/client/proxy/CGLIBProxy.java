package com.jxin.rpc.client.proxy;

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
public class CGLIBProxy implements MethodInterceptor {
    public Object getProxy(Class clazz){
        // CGLIB增强类对象
        final Enhancer enhancer = new Enhancer();
        // 设置增强类型
        enhancer.setSuperclass(clazz);
        // 定义代理逻辑对象为当前对象，要求当前对象实现MethodInterceptor方法
        enhancer.setCallback(this);
        return enhancer.create();
    }
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return methodProxy.invokeSuper(o,objects);
    }
}
