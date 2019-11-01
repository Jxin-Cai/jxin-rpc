package com.jxin.rpc.core.util.serializer.impl;

import com.jxin.rpc.core.call.msg.mark.RegisterServerMark;
import com.jxin.rpc.core.consts.SerializerEnum;
import com.jxin.rpc.core.exc.SerializeExc;
import com.jxin.rpc.core.util.serializer.ProtoStuffUtil;
import com.jxin.rpc.core.util.serializer.Serializer;

import java.nio.ByteBuffer;

/**
 * 服务标识列表序列化实现类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 20:38
 */
public class RegisterServerMarkSerializer implements Serializer<RegisterServerMark> {
    @Override
    public void serialize(RegisterServerMark obj, byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        final byte[] markByteArr = ProtoStuffUtil.serialize(obj);
        buffer.putInt(markByteArr.length);
        buffer.put(markByteArr);
    }

    @Override
    public RegisterServerMark deserialize(byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        final int markByteArrLen = buffer.getInt();
        final byte [] markByteArr = new byte[markByteArrLen];
        buffer.get(markByteArr);
        final RegisterServerMark registerServerMark = ProtoStuffUtil.deserialize(markByteArr, RegisterServerMark.class);

        if(registerServerMark == null){
            throw new SerializeExc("non null remoteServerMark");
        }
        return registerServerMark;
    }

    @Override
    public Integer size(RegisterServerMark obj) {
        return Integer.BYTES + ProtoStuffUtil.serialize(obj).length;
    }

    @Override
    public Integer getType() {
        return SerializerEnum.REGISTER_SERVER_MARK.getType();
    }

    @Override
    public Class<RegisterServerMark> getObjectClass() {
        return RegisterServerMark.class;
    }
}
