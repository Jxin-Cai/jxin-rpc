package com.jxin.rpc.center.server.hander.impl;

import com.jxin.rpc.center.feign.ForwordFeign;
import com.jxin.rpc.center.server.CenterContext;
import com.jxin.rpc.center.server.CenterContextSub;
import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.ReqMsg;
import com.jxin.rpc.core.call.msg.header.Header;
import com.jxin.rpc.core.call.msg.header.RspHeader;
import com.jxin.rpc.core.call.msg.mark.MethodMark;
import com.jxin.rpc.core.consts.ProviderEnum;
import com.jxin.rpc.core.consts.RspStatusEnum;
import com.jxin.rpc.core.server.hander.ProviderHander;
import com.jxin.rpc.core.util.serializer.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 代理端提供者执行器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:39
 */
@Slf4j
public class AgentProviderHander implements ProviderHander, CenterContextSub {
    /**空的字节码数组*/
    private static final byte[] EMPTY_BYTE_ARR = new byte[0];
    /**代理中心上下文*/
    private CenterContext centerContext;
    @Override
    public MsgContext handle(MsgContext msg) {
        final Header header = msg.getHeader();
        // 从body中反序列化出serverMark
        final ReqMsg reqMsg = SerializeUtil.parse(msg.getBody());
        try {
            if(centerContext.getApplicationName().equals(reqMsg.getServerMark().getApplication())){
                // 服务存在校验
                serviceExistValidate(reqMsg);
                // 调整为服务端
                header.setProviderType(ProviderEnum.SERVER_PROVIDER.getType());
                return centerContext.getLocalForwordFeign().forwordRemoteService(msg);
            }
            final List<ForwordFeign> forwordFeigns = centerContext.getApplicationFeignListMap()
                                                                  .get(reqMsg.getServerMark().getApplication());
            assert CollectionUtils.isNotEmpty(forwordFeigns) : "none register server : " + reqMsg.getServerMark().getApplication();
            final int abs = Math.abs(header.getRequestId().hashCode()) % forwordFeigns.size();
            return forwordFeigns.get(abs).forwordRemoteService(msg);
        } catch (Exception e) {
            // 发生异常,返回UNKNOWN_ERROR错误响应。
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
     * 服务存在校验
     * @param  reqMsg 请求消息
     * @throws AssertionError 请求的服务不存在
     * @author 蔡佳新
     */
    private void serviceExistValidate(ReqMsg reqMsg) {
        final List<MethodMark> methodMarks = centerContext.getServiceContext().get(reqMsg.getServerMark().getInterfaceName());
        assert !CollectionUtils.isEmpty(methodMarks) : "none interface: " + reqMsg.getServerMark().getInterfaceName();
        assert methodMarks.contains(reqMsg.getMethodMark()) : String.format("none method: %s.%s" ,
                                                              reqMsg.getServerMark().getInterfaceName(),
                                                              reqMsg.getMethodMark().getMethodName());
    }


    @Override
    public int type() {
        return ProviderEnum.AGENT_PROVIDER.getType();
    }

    @Override
    public void setCenterContext(CenterContext centerContext) {
        this.centerContext= centerContext;
    }
}
