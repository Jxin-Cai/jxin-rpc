package com.jxin.rpc.core.util.serializer.impl;

import com.jxin.rpc.core.call.msg.ReqMsg;
import com.jxin.rpc.core.consts.SerializerEnum;
import com.jxin.rpc.core.exc.SerializeExc;
import com.jxin.rpc.core.mark.MethodMark;
import com.jxin.rpc.core.mark.ServerMark;
import com.jxin.rpc.core.util.serializer.ArgMarkUtil;
import com.jxin.rpc.core.util.serializer.ProtoStuffUtil;
import com.jxin.rpc.core.util.serializer.Serializer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;

/**
 * 请求消息序列化实现类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 20:38
 */
public class ReqMsgSerializer implements Serializer<ReqMsg> {
    @Override
    public void serialize(ReqMsg obj, byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        // serverMark
        final byte[] serverMarkByteArr = ProtoStuffUtil.serialize(obj.getServerMark());
        buffer.putInt(serverMarkByteArr.length);
        buffer.put(serverMarkByteArr);
        // methodMark
        final byte[] methodMarkByteArr = ProtoStuffUtil.serialize(obj.getMethodMark());
        buffer.putInt(methodMarkByteArr.length);
        buffer.put(methodMarkByteArr);
        // ArgArr
        if(ArrayUtils.isEmpty(obj.getArgArr())){
            return;
        }
        for (Object o : obj.getArgArr()) {
            final byte[] argByteArr = ProtoStuffUtil.serialize(o);
            buffer.putInt(argByteArr.length);
            buffer.put(argByteArr);
        }
    }

    @Override
    public ReqMsg deserialize(byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        // serverMark
        final int serverMarkLen = buffer.getInt();
        final byte [] serverMarkByteArr = new byte[serverMarkLen];
        buffer.get(serverMarkByteArr);
        final ServerMark serverMark = ProtoStuffUtil.deserialize(serverMarkByteArr, ServerMark.class);

        if(serverMark == null){
            throw new SerializeExc("non null serverMark");
        }
        // methodMark
        final int methodMarkLen = buffer.getInt();
        final byte [] methodMarkByteArr = new byte[methodMarkLen];
        buffer.get(methodMarkByteArr);
        final MethodMark methodMark = ProtoStuffUtil.deserialize(methodMarkByteArr, MethodMark.class);

        if(methodMark == null){
            throw new SerializeExc("non null methodMark");
        }
        // argArr
        final String argMarkArrStr = methodMark.getArgMarkArrStr();
        if(StringUtils.isBlank(argMarkArrStr)){
            return ReqMsg.builder().serverMark(serverMark).build();
        }

        final String[] argMarArr = argMarkArrStr.split(",");
        final Object[] argArr = new Object[argMarArr.length];
        for (int i = 0; i < argMarArr.length; i++) {
            final int argLen = buffer.getInt();
            final byte [] argMarByteArr = new byte[argLen];
            final Class<?> clazz = ArgMarkUtil.getClazz(argMarArr[i]);
            if(clazz == null){
                throw new SerializeExc("non null clazz");
            }
            argArr[i] = ProtoStuffUtil.deserialize(argMarByteArr, clazz);
        }

        return ReqMsg.builder()
                     .serverMark(serverMark)
                     .methodMark(methodMark)
                     .argArr(argArr)
                     .build();
    }

    @Override
    public Integer size(ReqMsg obj) {
        final int serverMarkLen = Integer.BYTES + ProtoStuffUtil.serialize(obj.getServerMark()).length;
        if(ArrayUtils.isEmpty(obj.getArgArr())){
            return serverMarkLen;
        }
        int argLen = 0;
        for (Object o : obj.getArgArr()) {
            argLen += Integer.BYTES + ProtoStuffUtil.serialize(o).length;
        }
        return serverMarkLen + argLen;
    }

    @Override
    public Integer getType() {
        return SerializerEnum.REQ_MSG.getType();
    }

    @Override
    public Class<ReqMsg> getObjectClass() {
        return ReqMsg.class;
    }
}
