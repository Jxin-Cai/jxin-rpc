package com.jxin.rpc.core.proxy;

import com.google.common.collect.Maps;
import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.ReqMsg;
import com.jxin.rpc.core.call.msg.RspMsg;
import com.jxin.rpc.core.call.msg.header.ReqHeader;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.call.msg.mark.MethodMark;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.consts.ProVersionConsts;
import com.jxin.rpc.core.consts.ProviderEnum;
import com.jxin.rpc.core.consts.RspStatusEnum;
import com.jxin.rpc.core.exc.RPCExc;
import com.jxin.rpc.core.util.IdUtil;
import com.jxin.rpc.core.util.serializer.SerializeUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 动代要实现的业务
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
@Slf4j
public abstract class AbstractFeignProxy{
    /**服务标识*/
    protected ServerMark serverMark;
    /**方法标识 map*/
    private Map<String/*methodName*/, MethodMark> methodMarkMap = Maps.newHashMap();
    /**消息发送器*/
    protected Sender sender;

    /**
     * 获取代理类
     * @param  clazz      类的字节码对象
     * @param  serverMark 服务标识
     * @param  sender     消息发送器
     * @return 代理类实例
     * @author 蔡佳新
     */
    public abstract Object getProxy(Class<?> clazz,
                                    ServerMark serverMark,
                                    Sender sender) ;

    /**
     * 远程调用真逻辑
     * @param  method 方法实例
     * @param  args   参数数组
     * @throws RPCExc 发送消息异常,或者返回消息异常
     * @return 调用返回参数
     */
    protected Object invokeRemote(Method method, Object[] args) {
        final ReqMsg reqMsg = warpReqMsg(method, args);
        final MsgContext reqMsgContext = warpMsgContext(reqMsg);
        final MsgContext rspMsgContext;

        try {
            rspMsgContext = sender.send(reqMsgContext).get();
        } catch (Exception e) {
            throw new RPCExc(e.getMessage());
        }

        final RspHeader rspHeader = (RspHeader) rspMsgContext.getHeader();
        if(RspStatusEnum.RES_CODE_200.getCode().equals(rspHeader.getCode())) {
            final RspMsg rspMsg = SerializeUtil.parse(rspMsgContext.getBody());
            return rspMsg.getReturnArg();
        }
        throw new RPCExc(rspHeader.getErrMsg());
    }

    //************************************************warpReqMsg********************************************************
    /**
     * 封装请求消息实例
     * @param  method 方法实例
     * @param  args   参数列表
     * @return 请求消息实例
     * @author 蔡佳新
     */
    private ReqMsg warpReqMsg(Method method, Object[] args){
        MethodMark methodMark = methodMarkMap.get(method.getName());
        if(methodMark == null){
            // 往<code>methodMarkMap</code>中添加该方式的标记实例,并返回该标记实例
            methodMark = putMethodMarkMap(method);
        }
        return ReqMsg.builder().serverMark(serverMark)
                               .methodMark(methodMark)
                               .argArr(args)
                               .build();
    }

    /**
     * 往<code>methodMarkMap</code>中添加该方式的标记实例,并返回该标记实例
     * @param  method 方法实例
     * @return 方法标记实例
     * @author 蔡佳新
     */
    private MethodMark putMethodMarkMap(Method method) {
        final MethodMark result = MethodMark.builder()
                                            .methodName(method.getName())
                                            .argMarkArrStr(MethodMark.joinArgMarkArrToString(method.getParameterTypes()))
                                            .build();
        methodMarkMap.put(result.getMethodName(), result);
        return result;
    }
    //**********************************************warpMsgContext******************************************************

    /**
     * 封装消息上下文
     * @param  reqMsg 请求消息
     * @return 消息上下文
     * @author 蔡佳新
     */
    private MsgContext warpMsgContext(ReqMsg reqMsg){
        return MsgContext.builder()
                         .header(ReqHeader.builder()
                                          .requestId(IdUtil.getUUID())
                                          .version(ProVersionConsts.VERSION_1)
                                          .type(ProviderEnum.AGENT_PROVIDER.getType())
                                          .build())
                         .body(SerializeUtil.serialize(reqMsg))
                         .build();
    }

}
