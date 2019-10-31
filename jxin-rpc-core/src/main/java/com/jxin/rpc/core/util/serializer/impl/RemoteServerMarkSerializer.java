package com.jxin.rpc.core.util.serializer.impl;

import com.jxin.rpc.core.call.msg.mark.RemoteServerMark;
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
public class RemoteServerMarkSerializer implements Serializer<RemoteServerMark> {
    @Override
    public void serialize(RemoteServerMark obj, byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        final byte[] markByteArr = ProtoStuffUtil.serialize(obj);
        buffer.putInt(markByteArr.length);
        buffer.put(markByteArr);
    }

    @Override
    public RemoteServerMark deserialize(byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        final int markByteArrLen = buffer.getInt();
        final byte [] markByteArr = new byte[markByteArrLen];
        buffer.get(markByteArr);
        final RemoteServerMark remoteServerMark = ProtoStuffUtil.deserialize(markByteArr, RemoteServerMark.class);

        if(remoteServerMark == null){
            throw new SerializeExc("non null remoteServerMark");
        }
        return remoteServerMark;
    }

    @Override
    public Integer size(RemoteServerMark obj) {
        return ProtoStuffUtil.serialize(obj).length;
    }

    @Override
    public Integer getType() {
        return SerializerEnum.SERVER_MARK.getType();
    }

    @Override
    public Class<RemoteServerMark> getObjectClass() {
        return RemoteServerMark.class;
    }
}
