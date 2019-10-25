package com.jxin.rpc.client.hander;

import com.jxin.rpc.client.call.Sender;
import com.jxin.rpc.client.call.SenderSub;
import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.ReqMsg;
import com.jxin.rpc.core.call.msg.RspMsg;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.consts.RspStatusEnum;
import com.jxin.rpc.core.exc.RPCExc;

import java.util.concurrent.ExecutionException;

/**
 * 抽象消费者执行器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/25 20:09
 */
public abstract class AbstractConsumerHander implements SenderSub {
    /**消息发送器*/
    protected Sender sender;

    /**
     * 调用远程接口
     * @param  reqMsg 请求参数
     * @return 响应的消息体 {@link RspMsg}
     * @author 蔡佳新
     */
    protected byte[] invokeRemote(ReqMsg reqMsg) throws InterruptedException{
        final MsgContext reqMsgContext = warpMsgContext(reqMsg);
        try {
            final MsgContext rspMsgContext = sender.send(reqMsgContext).get();
            RspHeader rspHeader = (RspHeader) rspMsgContext.getHeader();
            if(RspStatusEnum.RES_CODE_200.getCode().equals(rspHeader.getCode())) {
                return rspMsgContext.getBody();
            }
            throw new RPCExc(rspHeader.getErrMsg());
        } catch (ExecutionException e) {
            throw new RPCExc(e.getCause());
        }
    }

    protected abstract MsgContext warpMsgContext(ReqMsg reqMsg);

    @Override
    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
