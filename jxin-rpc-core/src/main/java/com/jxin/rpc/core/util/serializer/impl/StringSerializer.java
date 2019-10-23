package com.jxin.rpc.core.util.serializer.impl;

import com.jxin.rpc.core.consts.SerializerEnum;
import com.jxin.rpc.core.util.serializer.Serializer;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 字符串序列化实现类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 21:23
 */
public class StringSerializer implements Serializer<String> {

    @Override
    public void serialize(String obj, byte[] bytes, int offset, int length) {
        if(StringUtils.isBlank(obj)){
            return ;
        }
         final byte [] dataBytes = obj.getBytes(StandardCharsets.UTF_8);
         System.arraycopy(dataBytes, 0, bytes, offset, dataBytes.length);
    }

    @Override
    public String deserialize(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, StandardCharsets.UTF_8);
    }

    @Override
    public Integer size(String obj) {
        return obj.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public Integer getType() {
        return SerializerEnum.STR.getType();
    }

    @Override
    public Class<String> getObjectClass() {
        return String.class;
    }
}
