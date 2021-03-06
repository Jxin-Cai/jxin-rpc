package com.jxin.rpc.core.util.serializer.impl;

import com.jxin.rpc.core.call.msg.RspMsg;
import com.jxin.rpc.core.consts.SerializerEnum;
import com.jxin.rpc.core.exc.SerializeExc;
import com.jxin.rpc.core.call.msg.mark.ReturnArgMark;
import com.jxin.rpc.core.util.serializer.ArgMarkUtil;
import com.jxin.rpc.core.util.serializer.ProtoStuffUtil;
import com.jxin.rpc.core.util.serializer.Serializer;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 返回消息序列化实现类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 20:38
 */
public class RspMsgSerializer implements Serializer<RspMsg> {
    @Override
    public void serialize(RspMsg obj, byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        // returnArgMark
        final byte[] returnArgMarkByteArr = ProtoStuffUtil.serialize(obj.getReturnArgMark());
        buffer.putInt(returnArgMarkByteArr.length);
        buffer.put(returnArgMarkByteArr);
        // returnArg
        final byte[] returnArgByteArr = getReturnArgByteArr(obj.getReturnArgMark(), obj.getReturnArg());
        buffer.putInt(returnArgByteArr.length);
        buffer.put(returnArgByteArr);
    }

    /**
     * 获得 返回参数列表的二进制数组
     * @param  returnArg 返回参数列表
     * @return 返回参数列表的二进制数组
     * @author 蔡佳新
     */
    private byte[] getReturnArgByteArr(ReturnArgMark returnArgMark, Object returnArg) {
        final byte[] result;
        if(returnArg == null){
            return EMPTY_BYTE_ARR;
        }
        if(returnArgMark.isMulti()){
            final Class<?> clazz = ArgMarkUtil.getClazz(returnArgMark.getClassMark());
            result = ProtoStuffUtil.serializeList(returnArg, clazz);
        }else {
            result = ProtoStuffUtil.serialize(returnArg);
        }
        return result;
    }

    @Override
    public RspMsg deserialize(byte[] bytes, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        final int returnArgMarkLen = buffer.getInt();
        final byte [] returnArgMarkByteArr = new byte[returnArgMarkLen];
        buffer.get(returnArgMarkByteArr);
        final ReturnArgMark returnArgMark = ProtoStuffUtil.deserialize(returnArgMarkByteArr, ReturnArgMark.class);
        if(returnArgMark == null){
            throw new SerializeExc("non null returnArgMark");
        }

        final Class<?> clazz = ArgMarkUtil.getClazz(returnArgMark.getClassMark());
        final int returnArgLen = buffer.getInt();
        final byte [] returnArgByteArr = new byte[returnArgLen];
        buffer.get(returnArgByteArr);
        // list序列化时
        if(returnArgMark.isMulti()){
            final List<?> returnArgList = ProtoStuffUtil.deserializeList(returnArgByteArr, clazz);
            return RspMsg.builder()
                         .returnArgMark(returnArgMark)
                         .returnArg(returnArgList)
                         .build();
        }
        // 单实例序列化时
        final Object returnArg = ProtoStuffUtil.deserialize(returnArgByteArr, clazz);
        return RspMsg.builder()
                     .returnArgMark(returnArgMark)
                     .returnArg(returnArg)
                     .build();
    }

    @Override
    public Integer size(RspMsg obj) {
        return Integer.BYTES + ProtoStuffUtil.serialize(obj.getReturnArgMark()).length
               + Integer.BYTES + getReturnArgByteArr(obj.getReturnArgMark(), obj.getReturnArg()).length;
    }

    @Override
    public Integer getType() {
        return SerializerEnum.RSP_MSG.getType();
    }

    @Override
    public Class<RspMsg> getObjectClass() {
        return RspMsg.class;
    }
}
