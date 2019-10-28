package com.jxin.rpc.core.util.serializer;

import com.jxin.rpc.core.consts.SerializerEnum;
import com.jxin.rpc.core.exc.SerializeExc;
import lombok.extern.slf4j.Slf4j;

/**
 * 序列化工具
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:41
 */
@Slf4j
public class SerializeUtil {

    /**
     * 序列化对象
     * 注: byte[] 中, [0]索引位放的是序列化器的类型 type的值
     * @param  obj 被序列化的对象
     * @param  <T> 对象的类型
     * @return 该对象的二进制数组
     * @author 蔡佳新
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        final Serializer<T> serializer = SerializerEnum.getByClazz((Class<T>)obj.getClass());
        if(serializer == null) {
            throw new SerializeExc(String.format("non null serializer, clazz: %s!", obj.getClass().toString()));
        }
        final byte [] result = new byte [serializer.size(obj) + 1];
        result[0] = serializer.getType().byteValue();
        serializer.serialize(obj, result, 1, result.length - 1);
        return result;
    }
    /**
     * 反序列化二进制数组
     * @param  buffer 二进制数组
     * @param  <T>    对象的类型
     * @return 反序列化出的对象
     * @author 蔡佳新
     */
    public static <T> T parse(byte[] buffer) {
        return parse(buffer, 0, buffer.length);
    }
    /**
     * 指定获取的类型,反序列化二进制数组
     * @param  buffer 二进制数组
     * @param  <T>    对象的类型
     * @return 反序列化出的对象
     * @author 蔡佳新
     */
    public static <T> T parse(byte[] buffer, Class<T> clazz) {
        return parse(buffer, 1, buffer.length - 1, clazz);
    }
    /**
     * 一)
     * 反序列化二进制数组
     * 1. 提取出序列化器的type
     * 2. 根据type获取clazz类型
     * @param  buffer 二进制数组
     * @param  offset 偏移量
     * @param  length 长度
     * @param  <T>    对象的类型
     * @return 反序列化出的对象
     * @author 蔡佳新
     */
    @SuppressWarnings("unchecked")
    private static <T> T parse(byte[] buffer, int offset, int length) {
        final Byte type = buffer[0];
        final Class<?> clazz = SerializerEnum.getByType(type.intValue());
        if(clazz == null) {
            throw new SerializeExc("non null clazz, type: %d!", type);
        }
        return parse(buffer, offset + 1, length - 1, (Class<T>) clazz);
    }
    /**
     * 二)
     * 反序列化二进制数组
     * 1.根据class反序列出对象
     * @param  buffer 二进制数组
     * @param  offset 偏移量
     * @param  length 长度
     * @param  clazz  反序列后出的对象的类的字节码对象
     * @param  <T>    对象的类型
     * @return 反序列化出的对象
     * @author 蔡佳新
     */
    @SuppressWarnings("unchecked")
    private static <T> T parse(byte[] buffer, int offset, int length, Class<T> clazz) {
        final Serializer<T> serializer = SerializerEnum.getByClazz(clazz);
        if(serializer == null){
            throw new SerializeExc("non null clazz, clazz: %d!", clazz.getCanonicalName());
        }
        final T obj = serializer.deserialize(buffer, offset, length);
        if (clazz.isAssignableFrom(obj.getClass())) {
            return obj;
        } else {
            throw new SerializeExc("clazz nonentity!");
        }
    }

}
