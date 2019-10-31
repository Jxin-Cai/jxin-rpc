package com.jxin.rpc.center.feign;

import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.RspMsg;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.call.msg.mark.MethodMark;
import com.jxin.rpc.core.consts.RspStatusEnum;
import com.jxin.rpc.core.exc.RPCExc;
import com.jxin.rpc.core.util.serializer.SerializeUtil;

import java.util.List;
import java.util.Map;

/**
 * 请求转发客户端 桩
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/30 11:52
 */
public interface ForwordFeign {
    /**
     * 代理端请求转发
     * @param  reqMsgContext 请求消息上下文
     * @return rspMsgContext
     * @throws RPCExc 转发请求异常
     * @author 蔡佳新
     */
    default MsgContext forwordRemoteService(MsgContext reqMsgContext)  {
        try {
            return getSender().send(reqMsgContext).get();
        } catch (Exception e) {
            throw new RPCExc(e);
        }
    }
    /**
     * 请求本地服务
     * @param  reqMsgContext 请求消息上下文
     * @return rspMsgContext
     * @throws RPCExc 请求异常
     * @author 蔡佳新
     */
    default Map<String/*interfaceName*/, List<MethodMark>> pullRegisterService(MsgContext reqMsgContext)  {
        try {
            final MsgContext rspMsgContext =  getSender().send(reqMsgContext).get();
            final RspHeader rspHeader = (RspHeader) rspMsgContext.getHeader();
            if(!RspStatusEnum.RES_CODE_200.getCode().equals(rspHeader.getCode())) {
                throw new RPCExc(rspHeader.getErrMsg());
            }
            final RspMsg rspMsg = SerializeUtil.parse(rspMsgContext.getBody());
            return (Map<String/*interfaceName*/, List<MethodMark>>)rspMsg.getReturnArg();
        } catch (Exception e) {
            throw new RPCExc(e);
        }
    }
    /**
     * 推送远程服务
     * @param  reqMsgContext 请求消息上下文
     * @throws RPCExc 请求异常
     * @author 蔡佳新
     */
    default void pushRemoteService(MsgContext reqMsgContext)  {
        try {
            final MsgContext rspMsgContext = getSender().send(reqMsgContext).get();
            final RspHeader rspHeader = (RspHeader) rspMsgContext.getHeader();
            if(!RspStatusEnum.RES_CODE_200.getCode().equals(rspHeader.getCode())) {
                throw new RPCExc(rspHeader.getErrMsg());
            }
        } catch (Exception e) {
            throw new RPCExc(e);
        }
    }
    /**
     * 获取消息发送器
     * @return 消息发送器
     * @author 蔡佳新
     */
    Sender getSender();
}
