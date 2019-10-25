package com.jxin.rpc.client.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk自带的动代实现
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
@Slf4j
public class SDKProxy implements InvocationHandler {
    /**要代理的目标实例*/
    private Object target;

    public Object getProxy(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(SDKProxy.class.getClassLoader(),
                target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if(target != null){
            result = method.invoke(target, args);
        }
        return result;
    }
}
