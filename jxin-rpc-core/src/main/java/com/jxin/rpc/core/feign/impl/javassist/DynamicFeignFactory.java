package com.jxin.rpc.core.feign.impl.javassist;

import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.exc.InitFeignExc;
import com.jxin.rpc.core.feign.FeignFactory;
import com.jxin.rpc.core.proxy.impl.sdk.FeignProxy;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * 桩(装)工厂类 javassist框架实现类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/28 16:38
 */
public class DynamicFeignFactory implements FeignFactory {
    /**实现类的包路径*/
    private static final String INSTANCE_CLASS_PKG_PATH = "com.jxin.rpc.core.feign.instance";
    @Override
    public <T> T createFeign(Sender sender, Class<T> insterface, ServerMark serverMark) {
        // 填充模板
        final String insterfaceFullName = serverMark.getInterfaceName();
        final String insterfaceName = insterface.getSimpleName();
        final String feignName = insterfaceName + "Feign";

        final ClassPool pool = ClassPool.getDefault();

        final Class<?> clazz;
        try {
            // 1. 创建一个空类
            final CtClass ccFeign = pool.makeClass(INSTANCE_CLASS_PKG_PATH + "." + feignName);
            // 2. 获取接口
            final CtClass ccInterface = pool.getCtClass(insterfaceFullName);
            ccFeign.setInterfaces(new CtClass[]{ccInterface});
            final CtMethod[] methods = ccInterface.getDeclaredMethods();
            for (CtMethod method : methods) {
                ccFeign.addMethod(new CtMethod(method.getReturnType(),
                                               method.getName(),
                                               method.getParameterTypes(),
                                               ccFeign));
            }

            // 3.生成类的字节码对象
            clazz = ccFeign.toClass();
        } catch (Exception e) {
            throw new InitFeignExc(e);
        }
        final Object proxy = new FeignProxy().getProxy(clazz, serverMark, sender);
        // 返回这个桩
        return (T) proxy;
    }
}
