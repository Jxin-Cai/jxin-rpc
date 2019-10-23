package com.jxin.rpc.core.util.serializer;


import com.jxin.rpc.core.inject.Nullable;

/**
 * 序列化接口
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 21:07
 */
public interface Serializer<T> {

    /**
     * 序列化对象
     * @param  obj 待序列化的对象
     * @param  bytes 存放序列化数据的字节数组
     * @param  offset 数组的偏移量，从这个位置开始写入序列化数据
     * @param  length 对象序列化后的长度，也就是{@link Serializer#size(java.lang.Object)}方法的返回值。
     * @author 蔡佳新
     */
    @Nullable
    void serialize(@Nullable T obj, byte[] bytes, int offset, int length);

    /**
     * 反序列化对象
     * @param  bytes 存放序列化数据的字节数组
     * @param  offset 数组的偏移量，从这个位置开始写入序列化数据
     * @param  length 对象序列化后的长度
     * @return 反序列化之后生成的对象
     * @author 蔡佳新
     */
    @Nullable
    T deserialize(@Nullable byte[] bytes, int offset, int length);

    /**
     * 计算对象序列化后的长度，主要用于申请存放序列化数据的字节数组
     * @param  obj 待序列化的对象
     * @return 对象序列化后的长度
     * @author 蔡佳新
     */
    Integer size(T obj);

    /**
     * 获取序列化器类型
     * @return 序列化器类型
     * @author 蔡佳新
     */
    Integer getType();

    /**
     * 获取序列化的对象 的Class对象
     * @return 序列化对象类型的Class对象
     * @author 蔡佳新
     */
    Class<T> getObjectClass();
}
