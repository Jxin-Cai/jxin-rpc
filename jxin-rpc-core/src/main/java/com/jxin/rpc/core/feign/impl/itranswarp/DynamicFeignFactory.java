package com.jxin.rpc.core.feign.impl.itranswarp;

import com.itranswarp.compiler.JavaStringCompiler;
import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.exc.InitFeignExc;
import com.jxin.rpc.core.feign.FeignFactory;
import com.jxin.rpc.core.proxy.impl.sdk.FeignProxy;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 桩(装)工厂类 itranswarp框架实现类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/28 16:38
 */
public class DynamicFeignFactory implements FeignFactory {
    /**实现类的包路径*/
    private static final String INSTANCE_CLASS_PKG_PATH = "com.jxin.rpc.core.feign.instance";
    /**feign类java代码的字符串模板*/
    private static final String FEIGN_TEMP = "package " + INSTANCE_CLASS_PKG_PATH + ";\n" +
                                             "\n" +
                                             "import %s;\n" +
                                             "\n" +
                                             "public class %s implements %s {\n" +
                                             "\n" +
                                             "\n" +
                                             "    @Override\n" +
                                             "    public String hello() {\n" +
                                             "        return null;\n" +
                                             "    }" +
                                             "}";
    @Override
    public <T> T createFeign(Sender sender, Class<T> insterface, ServerMark serverMark) {
        // 填充模板
        final String insterfaceFullName = serverMark.getInterfaceName();
        final String insterfaceName = insterface.getSimpleName();
        final String feignName = insterfaceName + "Feign";

        // 获取代码字符串
        final String source = String.format(FEIGN_TEMP, insterfaceFullName, feignName, insterfaceName);
        final JavaStringCompiler compiler = new JavaStringCompiler();
        final Class<?> clazz;
        try {
            // 编译源代码
            final Map<String, byte[]> results = compiler.compile(feignName + ".java", source);
            // 加载编译好的类
            clazz = compiler.loadClass(INSTANCE_CLASS_PKG_PATH + "." + feignName, results);
        } catch (Exception e) {
            throw new InitFeignExc(e);
        }
        // 返回这个桩
        return (T) new FeignProxy().getProxy(clazz, serverMark, sender);
    }
}
