package com.jxin.rpc.core.server.hander.impl;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.RspMsg;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.call.msg.mark.MethodMark;
import com.jxin.rpc.core.call.msg.mark.ReturnArgMark;
import com.jxin.rpc.core.consts.ProviderEnum;
import com.jxin.rpc.core.consts.RspStatusEnum;
import com.jxin.rpc.core.exc.RPCExc;
import com.jxin.rpc.core.scan.ApplicationContext;
import com.jxin.rpc.core.scan.ApplicationContextSub;
import com.jxin.rpc.core.server.hander.ProviderHander;
import com.jxin.rpc.core.util.serializer.ArgMarkUtil;
import com.jxin.rpc.core.util.serializer.SerializeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

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
        final Map<String, List<MethodMark>> allService = applicationContext.getAllService();
        return createMsgContext(header, allService);
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
        if(!(returnObj instanceof Iterable)){
            throw new RPCExc("register server err");
        }
        final Iterable returnIte = (Iterable)returnObj;
        final Class<?> clazz = returnIte.iterator().next().getClass();
        final ReturnArgMark returnArgMark = ReturnArgMark.builder()
                                                         .multi(true)
                                                         .classMark(ArgMarkUtil.getMark(clazz))
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
        return ProviderEnum.REGISTER_PROVIDER.getType();
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
