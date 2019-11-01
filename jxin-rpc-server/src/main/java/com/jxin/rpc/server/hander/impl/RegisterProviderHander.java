package com.jxin.rpc.server.hander.impl;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.RspMsg;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.call.msg.mark.MethodMark;
import com.jxin.rpc.core.call.msg.mark.RegisterServerMark;
import com.jxin.rpc.core.call.msg.mark.ReturnArgMark;
import com.jxin.rpc.core.consts.ProviderEnum;
import com.jxin.rpc.core.consts.RspStatusEnum;
import com.jxin.rpc.server.scan.ApplicationContext;
import com.jxin.rpc.server.scan.ApplicationContextSub;
import com.jxin.rpc.core.server.hander.ProviderHander;
import com.jxin.rpc.core.util.serializer.ArgMarkUtil;
import com.jxin.rpc.core.util.serializer.SerializeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static com.jxin.rpc.core.util.serializer.Serializer.EMPTY_BYTE_ARR;

/**
 * 服务端提供者执行器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:39
 */
@Slf4j
public class RegisterProviderHander implements ProviderHander, ApplicationContextSub {
    /**服务上下文*/
    private ApplicationContext applicationContext;
    @Override
    public MsgContext handle(MsgContext msg) {
        final Header header = msg.getHeader();
        if(applicationContext == null){
            return createMsgContext(header, null);
        }
        final Map<String, List<MethodMark>> allService = applicationContext.getAllService();

        return createMsgContext(header, RegisterServerMark.builder()
                                                          .registerServiceMap(allService)
                                                          .build());
    }

    /**
     * 创建响应消息上下文
     * @param  header             请求头
     * @param  registerServerMark 注册的服务标识
     * @return 响应消息上下文
     * @author 蔡佳新
     */
    private MsgContext createMsgContext(Header header,
                                        RegisterServerMark registerServerMark) {
        return MsgContext.builder()
                         .header(RspHeader.builder()
                                          .type(type())
                                          .version(header.getVersion())
                                          .requestId(header.getRequestId())
                                          .code(RspStatusEnum.RES_CODE_200.getCode())
                                          .build())
                         .body(registerServerMark == null? EMPTY_BYTE_ARR : SerializeUtil.serialize(registerServerMark))
                         .build();
    }

    @Override
    public int type() {
        return ProviderEnum.REGISTER_PROVIDER.getType();
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
