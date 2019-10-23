package com.jxin.rpc.core.call.msg.hander.impl;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.ReqMsg;
import com.jxin.rpc.core.call.msg.hander.ReqMsgHander;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.consts.ReqEnum;
import com.jxin.rpc.core.consts.RspStatusEnum;
import com.jxin.rpc.core.util.serializer.SerializeUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * rpc请求消息处理器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:39
 */
@Slf4j
public class RPCReqMsgHander implements ReqMsgHander {
    /**空的字节码数组*/
    private static final byte[] EMPTY_BYTE_ARR = new byte[0];
    @Override
    public MsgContext handle(MsgContext msg) {
        final Header header = msg.getHeader();
        // 从payload中反序列化RpcRequest
        final ReqMsg reqMsg = SerializeUtil.parse(msg.getBody());
        try {

            return MsgContext.builder().header(RspHeader.builder()
                                                        .type(type())
                                                        .version(header.getVersion())
                                                        .requestId(header.getRequestId())
                                                        .code(RspStatusEnum.RES_CODE_200.getCode())
                                                        .build())
                                       .body(EMPTY_BYTE_ARR)
                                       .build();
        } catch (Exception e) {
            // 发生异常，返回UNKNOWN_ERROR错误响应。
            log.warn("Exception: ", e);
            return MsgContext.builder().header(RspHeader.builder()
                                                        .type(type())
                                                        .version(header.getVersion())
                                                        .requestId(header.getRequestId())
                                                        .code(RspStatusEnum.RES_CODE_500.getCode())
                                                        .errMsg(e.getMessage())
                                                        .build())
                                       .body(EMPTY_BYTE_ARR)
                                       .build();
        }
    }

    @Override
    public int type() {
        return ReqEnum.CLIENT_REQ.getType();
    }
}
