package com.jxin.rpc.core.util.serializer;

import com.jxin.rpc.core.exc.SerializeExc;
import com.jxin.rpc.core.inject.NotEmpty;
import com.jxin.rpc.core.inject.NotNull;
import com.jxin.rpc.core.inject.Nullable;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * ProtoStuff 序列化器的工具类
 * @author  蔡佳新
 * @version 1.0
 * @since   jdk 1.8
 */
@Slf4j
public class ProtoStuffUtil {

    /**LinkedBuffer 默认长度*/
    private static final Integer LINKED_BUFFER_DEF_SIZE= 1024 * 1024;

    /**
     * 序列化的对象
     * @param  obj 要序列化的对象
     * @param  <T> 对象类型
     * @throws SerializeExc 序列化失败时
     * @return 序列化后的字节数组
     * @author 蔡佳新
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(@NotNull T obj){
        final LinkedBuffer buffer = LinkedBuffer.allocate(LINKED_BUFFER_DEF_SIZE);
        try {
            assert obj != null : "non null obj required";
            return ProtostuffIOUtil.toByteArray(obj,
                                                (Schema<T>) RuntimeSchema.getSchema(obj.getClass()),
                                                buffer);
        } catch (Exception e) {
            throw new SerializeExc(e, "单对象序列化失败, obj:%s", GsonUtil.GsonToStr(obj));
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化对象
     * @param  objByteArr 对象的字节数组
     * @param  clazz      对象的字节码类型
     * @param  <T>        对象类型
     * @throws SerializeExc  反序列化失败时
     * @return 对象实例
     * @author 蔡佳新
     */
    @Nullable
    public static <T> T deserialize(byte[] objByteArr, @NotNull Class<T> clazz){
        if(ArrayUtils.isEmpty(objByteArr)){
            return null;
        }

        final T result;
        try {
            assert clazz != null : "non null clazz required";
            result = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(objByteArr, result, RuntimeSchema.getSchema(clazz));
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SerializeExc(e, "单对象反序列化失败, clazz:{}", clazz.getCanonicalName());
        }
        return result;
    }

    /**
     * 序列化对象列表
     * @param  obj   要序列化的对象列表
     * @param  clazz 字节码类型
     * @param  <T>   对象类型
     * @throws SerializeExc  序列化失败时
     * @return 序列化后的字节数组
     * @author 蔡佳新
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serializeList(@NotEmpty Object obj, @NotNull Class<T> clazz) {
        if (obj == null) {
            throw new IllegalArgumentException("non null obj required");
        }
        final LinkedBuffer buffer = LinkedBuffer.allocate(LINKED_BUFFER_DEF_SIZE);
        byte[] result;
        try(final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ProtostuffIOUtil.writeListTo(bos,
                                         (List<T>)obj,
                                         RuntimeSchema.getSchema(clazz),
                                         buffer);
            result = bos.toByteArray();
        } catch (Exception e) {
            throw new SerializeExc(e, "多对象序列化失败, obj:{}", GsonUtil.GsonToStr(obj));
        }finally {
            buffer.clear();
        }
        return result;
    }

    /**
     * 反序列化对象列表
     * @param  objByteArr 对象字节数组
     * @param  clazz      字节码类型
     * @param  <T>        对象类型
     * @throws SerializeExc  反序列化失败时
     * @return 对象列表
     * @author 蔡佳新
     */
    public static <T> List<T> deserializeList(@NotEmpty byte[] objByteArr, @NotNull Class<T> clazz) {
        if (ArrayUtils.isEmpty(objByteArr)) {
            throw new IllegalArgumentException("non empty objByteArr required");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("non null clazz required");
        }
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        List<T> result;
        try {
            result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(objByteArr), schema);
        } catch (IOException e) {
            throw new SerializeExc(e, "多对象反序列化失败, clazz:{}", clazz.getCanonicalName());
        }
        return result;
    }
}
