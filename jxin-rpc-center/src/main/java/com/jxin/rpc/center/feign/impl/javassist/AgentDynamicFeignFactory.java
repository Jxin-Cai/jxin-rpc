package com.jxin.rpc.center.feign.impl.javassist;

import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.exc.InitFeignExc;
import com.jxin.rpc.core.feign.FeignFactory;
import javassist.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 桩(装)工厂类 javassist框架实现类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/28 16:38
 */
public class AgentDynamicFeignFactory implements FeignFactory {
    /**实现类的包路径*/
    private static final String INSTANCE_CLASS_PKG_PATH = "com.jxin.rpc.core.feign.instance";
    @Override
    public <T> T createFeign(Sender sender, Class<T> insterface, ServerMark serverMark) {
        // 填充模板
        final String feignName = upperCase(serverMark.getApplication()) + "Feign";
        final ClassPool pool = ClassPool.getDefault();
        final Object obj;
        try {

            // 1. 创建一个空类
            final CtClass ccFeign = pool.makeClass(INSTANCE_CLASS_PKG_PATH + "." + feignName);
            // Sender的字节码对象
            final CtClass ctClass = pool.get("com.jxin.rpc.core.call.Sender");
            // 空字节码对象数组
            final CtClass[] emptyClassArr = {};
            // 2. 新增一个字段 private Sender sender;
            final CtField param = new CtField(ctClass, "sender", ccFeign);
            param.setModifiers(Modifier.PRIVATE);
            ccFeign.addField(param);
            // 3. 添加无参的构造函数
            final CtConstructor defCons = new CtConstructor(emptyClassArr, ccFeign);
            defCons.setBody("{super();}");
            ccFeign.addConstructor(defCons);
            // 4. 添加有参的构造函数
            final CtConstructor cons = new CtConstructor(new CtClass[]{ctClass}, ccFeign);
            // $0=this / $1,$2,$3... 代表方法参数
            cons.setBody("{$0.sender = $1;}");
            ccFeign.addConstructor(cons);
            // 5. 获取接口
            final CtClass ccInterface = pool.getCtClass(insterface.getName());
            ccFeign.addInterface(ccInterface);
            // 6. 创建一个名为printName方法，无参数，无返回值，输出name值
            CtMethod ctMethod = new CtMethod(ctClass, "getSender", emptyClassArr, ccFeign);
            ctMethod.setModifiers(Modifier.PUBLIC);
            ctMethod.setBody("{return sender;}");
            ccFeign.addMethod(ctMethod);
            // 7.生成类的字节码对象
            final Class<?> clazz = ccFeign.toClass();
            final Class[] classes = {Sender.class};
            obj = clazz.getConstructor(classes).newInstance(sender);
        } catch (Exception e) {
            throw new InitFeignExc(e);
        }
        // 返回这个桩
        return (T) obj;
    }

    /**
     * 首字母大写
     * @param  str 字符串
     * @return 首字母大写后的字符串
     * @author 蔡佳新
     */
    private String upperCase(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        final char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        int len = ch.length;
        for (int i = 0; i < len; i++) {
            if (isLetter(ch[i])) {
                continue;
            }
            for (int j = i; j < len - 1; j++) {
                ch[j] = ch[j + 1];
            }
            len--;
        }
       return new String(ch,0,len);
    }

    private boolean isLetter(char ch) {
        return (64 <= ch && ch <= 90) || (96 <= ch && ch <= 122);
    }

}
