package com.jxin.rpc.core.serializer.impl;

import com.jxin.rpc.core.consts.DataEnum;
import com.jxin.rpc.core.serializer.Serializer;
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
    public void serialize(String entry, byte[] bytes, int offset, int length) {
        if(StringUtils.isBlank(entry)){
            return ;
        }
         final byte [] dataBytes = entry.getBytes(StandardCharsets.UTF_8);
         System.arraycopy(dataBytes, 0, bytes, offset, dataBytes.length);
    }

    @Override
    public String deserialize(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, StandardCharsets.UTF_8);
    }

    @Override
    public Integer size(String entry) {
        return entry.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public DataEnum type() {
        return DataEnum.STR;
    }

    @Override
    public Class<String> getSerializeClass() {
        return String.class;
    }
}
