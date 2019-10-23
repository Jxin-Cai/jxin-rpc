package com.jxin.rpc.core.util.serializer;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数标示工具
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 21:14
 */
@Slf4j
public class ArgMarkUtil {
    /**基础类的标示*/
    private static final Map<String/*类的标识符*/, Class<?>> BASE_MARK_MAP = new HashMap<>(8);
    static {
        BASE_MARK_MAP.put("B", byte.class);
        BASE_MARK_MAP.put("C", char.class);
        BASE_MARK_MAP.put("D", double.class);
        BASE_MARK_MAP.put("F", float.class);
        BASE_MARK_MAP.put("I", int.class);
        BASE_MARK_MAP.put("J", long.class);
        BASE_MARK_MAP.put("S", short.class);
        BASE_MARK_MAP.put("Z", boolean.class);
    }

    /**
     * 根据标示获取 类的字节码对象
     * @param  mark 参数标示
     * @return 类的字节码对象
     * @author 蔡佳新
     */
    public static Class<?> getClazz(String mark){
        final Class<?> clazz = BASE_MARK_MAP.get(mark);
        if(clazz != null){
            return clazz;
        }
        try {
            return Class.forName(mark);
        } catch (ClassNotFoundException e) {
           log.error(e.getMessage(), e);
        }
        return null;
    }
}
