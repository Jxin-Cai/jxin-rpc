package com.jxin.rpc.core.util.serializer.impl;

import com.jxin.rpc.core.consts.SerializerEnum;
import com.jxin.rpc.core.exc.SerializeExc;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.util.serializer.ProtoStuffUtil;
import com.jxin.rpc.core.util.serializer.Serializer;

import java.nio.ByteBuffer;

/**
 * 返回消息序列化实现类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 20:38
 */
public class ServerMarkSerializer implements Serializer<ServerMark> {
    @Override
    public void serialize(ServerMark obj, byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        final byte[] markByteArr = ProtoStuffUtil.serialize(obj);
        buffer.putInt(markByteArr.length);
        buffer.put(markByteArr);
    }

    @Override
    public ServerMark deserialize(byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        final int markByteArrLen = buffer.getInt();
        final byte [] markByteArr = new byte[markByteArrLen];
        buffer.get(markByteArr);
        final ServerMark serverMark = ProtoStuffUtil.deserialize(markByteArr, ServerMark.class);

        if(serverMark == null){
            throw new SerializeExc("non null serverMark");
        }
        return serverMark;
    }

    @Override
    public Integer size(ServerMark obj) {
        return ProtoStuffUtil.serialize(obj).length;
    }

    @Override
    public Integer getType() {
        return SerializerEnum.SERVER_MARK.getType();
    }

    @Override
    public Class<ServerMark> getObjectClass() {
        return ServerMark.class;
    }
}
