package com.jxin.rpc.server.hander.impl;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.ReqMsg;
import com.jxin.rpc.core.call.msg.RspMsg;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.call.msg.mark.RemoteServerMark;
import com.jxin.rpc.core.call.msg.mark.ReturnArgMark;
import com.jxin.rpc.core.consts.ProviderEnum;
import com.jxin.rpc.core.consts.RspStatusEnum;
import com.jxin.rpc.core.exc.InitFeignExc;
import com.jxin.rpc.core.server.hander.ProviderHander;
import com.jxin.rpc.core.util.serializer.ArgMarkUtil;
import com.jxin.rpc.core.util.serializer.SerializeUtil;
import com.jxin.rpc.server.scan.ApplicationContext;
import com.jxin.rpc.server.scan.ApplicationContextSub;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 生成远程 桩的执行器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:39
 */
@Slf4j
public class RemoteFeignProviderHander implements ProviderHander, ApplicationContextSub {
    /**服务上下文*/
    private ApplicationContext applicationContext;

    @Override
    public MsgContext handle(MsgContext msg) {
        final Header header = msg.getHeader();
        // 从body中反序列化出reqMsg
        final ReqMsg reqMsg = SerializeUtil.parse(msg.getBody());
        final RemoteServerMark remoteServerMark = (RemoteServerMark)reqMsg.getArgArr()[0];
        if(remoteServerMark == null || CollectionUtils.isEmpty(remoteServerMark.getRemoteServerList())){
            return createMsgContext(header, null);
        }

        assert applicationContext != null : "none applicationContext";
        try {
            applicationContext.injectRemoteService(remoteServerMark.getRemoteServerList());
        } catch (Exception e) {
            throw new InitFeignExc(e);
        }
        return createMsgContext(header, null);
    }

    /**
     * 创建响应消息上下文
     * @param  header    请求头
     * @param  returnObj 返回参数
     * @return 响应消息上下文
     * @author 蔡佳新
     */
    private MsgContext createMsgContext(Header header,
                                        Object returnObj) {
        final ReturnArgMark returnArgMark = ReturnArgMark.builder()
                                                         .multi(false)
                                                         .classMark(ArgMarkUtil.getMark(returnObj == null? Object.class : returnObj.getClass()))
                                                         .build();
        return MsgContext.builder()
                         .header(RspHeader.builder()
                                          .type(type())
                                          .version(header.getVersion())
                                          .requestId(header.getRequestId())
                                          .code(RspStatusEnum.RES_CODE_200.getCode())
                                          .build())
                         .body(SerializeUtil.serialize(RspMsg.builder()
                                                             .returnArgMark(returnArgMark)
                                                             .returnArg(returnObj)
                                                             .build()))
                         .build();
    }

    @Override
    public int type() {
        return ProviderEnum.REMOTE_FEIGN_PROVIDER.getType();
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
