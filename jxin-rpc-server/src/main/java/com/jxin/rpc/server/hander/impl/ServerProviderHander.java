package com.jxin.rpc.server.hander.impl;

import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.ReqMsg;
import com.jxin.rpc.core.call.msg.RspMsg;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.call.msg.mark.ReturnArgMark;
import com.jxin.rpc.core.consts.ProviderEnum;
import com.jxin.rpc.core.consts.RspStatusEnum;
import com.jxin.rpc.core.exc.RPCExc;
import com.jxin.rpc.server.scan.ApplicationContext;
import com.jxin.rpc.server.scan.ApplicationContextSub;
import com.jxin.rpc.core.server.hander.ProviderHander;
import com.jxin.rpc.core.util.serializer.ArgMarkUtil;
import com.jxin.rpc.core.util.serializer.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * 服务端提供者执行器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:39
 */
@Slf4j
public class ServerProviderHander implements ProviderHander, ApplicationContextSub {
    /**空的字节码数组*/
    private static final byte[] EMPTY_BYTE_ARR = new byte[0];
    /**服务上下文*/
    private ApplicationContext applicationContext;
    @Override
    public MsgContext handle(MsgContext msg) {
        final Header header = msg.getHeader();
        // 从body中反序列化出reqMsg
        final ReqMsg reqMsg = SerializeUtil.parse(msg.getBody());
        try {
            final Object service = applicationContext.getRegistServiceList(reqMsg.getServerMark().getInterfaceName());
            if(service == null){
                throw new RPCExc("non empty serviceList ");
            }

            final Method method = applicationContext.getRegistMethod(reqMsg.getServerMark().getInterfaceName())
                                                    .get(reqMsg.getMethodMark());
            final Object returnObj = method.invoke(service, reqMsg.getArgArr());
            return createMsgContext(header, returnObj);
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

    /**
     * 创建响应消息上下文
     * @param  header    请求头
     * @param  returnObj 返回参数
     * @return 响应消息上下文
     * @author 蔡佳新
     */
    private MsgContext createMsgContext(Header header,
                                        Object returnObj) {
        if(returnObj instanceof Iterable){
            final Iterable returnIte= (Iterable)returnObj;
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
        final ReturnArgMark returnArgMark = ReturnArgMark.builder()
                                                         .classMark(ArgMarkUtil.getMark(returnObj.getClass()))
                                                         .multi(false)
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

    /**
     * 获取方法实例
     * @param  methodName    方法名
     * @param  argMarkArrStr 参数标志数组字符串
     * @param  obj           对象
     * @return 方法实例
     * @throws NoSuchMethodException 方法不存在 
     * @author 蔡佳新
     */
    @Deprecated
    private Method getMethod(String methodName, String argMarkArrStr, Object obj) throws NoSuchMethodException {
        if(StringUtils.isBlank(argMarkArrStr)){
             return obj.getClass().getMethod(methodName);
        }
        final String[] argMarkArr = argMarkArrStr.split(",");
        final Class<?>[] clizzArr = new Class<?>[argMarkArr.length];
        for (int i = 0; i < argMarkArr.length; i++) {
            final Class<?> clazz = ArgMarkUtil.getClazz(argMarkArr[i]);
            if(clazz == null){
                throw new RPCExc("non null clazz ");
            }
            clizzArr[i] = clazz;
        }
       return obj.getClass().getMethod(methodName, clizzArr);
    }

    @Override
    public int type() {
        return ProviderEnum.SERVER_PROVIDER.getType();
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
