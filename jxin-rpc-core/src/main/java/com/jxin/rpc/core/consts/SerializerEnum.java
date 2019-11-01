package com.jxin.rpc.core.consts;

import com.google.common.collect.Maps;
import com.jxin.rpc.core.call.msg.ReqMsg;
import com.jxin.rpc.core.call.msg.RspMsg;
import com.jxin.rpc.core.call.msg.mark.RegisterServerMark;
import com.jxin.rpc.core.call.msg.mark.RemoteServerMark;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.util.serializer.Serializer;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;

import java.util.Collection;
import java.util.HashMap;

/**
 * 序列化实现枚举类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 21:45
 */

public enum SerializerEnum {
    /**字符串*/
    STR("字符串", 0, String.class),
    /**请求消息*/
    REQ_MSG("请求消息", 1, ReqMsg.class),
    /**返回消息*/
    RSP_MSG("返回消息", 2, RspMsg.class),
    /**服务标识*/
    SERVER_MARK("服务标示", 3, ServerMark.class),
    /**注册服务标示*/
    REGISTER_SERVER_MARK("注册服务标示", 4, RegisterServerMark.class),
    /**远程服务标示*/
    REMOTE_SERVER_MARK("远程服务标示", 5, RemoteServerMark.class),
    ;

    /**序列化器名称*/
    private final String name;
    /**序列化器类型*/
    private final Integer type;
    /**序列化器类型*/
    private final Class<?> clazz;

    /**根据type获取Clazz的map*/
    private static final HashMap<Integer/*type*/, Class<?>> TYPE_MAP = Maps.newHashMap();
    /**根据Clazz序列化器的map*/
    private static final HashMap<Class<?>/*clazz*/, Serializer<?>> CLAZZ_MAP = Maps.newHashMap();
    SerializerEnum(String name, Integer type, Class<?> clazz) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
    }
    static {
        // TYPE_MAP 和 CLAZZ_MAP  init
        for (SerializerEnum serializerEnum : SerializerEnum.values()) {
            TYPE_MAP.put(serializerEnum.getType(), serializerEnum.getClazz());
        }
        // serializer init
        final Collection<Serializer> serializerList = ServiceLoaderUtil.loadAll(Serializer.class);
        for (final Serializer serializer : serializerList) {
            CLAZZ_MAP.put(serializer.getObjectClass(), serializer);
        }
    }

    /**
     * 获取序列化实现枚举类实例,根据type
     * @param  type 序列化类型
     * @return 序列化器
     * @author 蔡佳新
     */
    public static Class<?> getByType(Integer type){
        return TYPE_MAP.get(type);
    }
    /**
     * 获取序列化实现枚举类实例,根据type
     * @param  clazz 被序列化的对象的类的字节码对象
     * @return 序列化器
     * @author 蔡佳新
     */
    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> getByClazz(Class<T> clazz){
        return (Serializer<T>) CLAZZ_MAP.get(clazz);
    }
    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
