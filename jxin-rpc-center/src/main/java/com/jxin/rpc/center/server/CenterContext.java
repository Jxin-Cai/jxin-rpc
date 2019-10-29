package com.jxin.rpc.center.server;

import com.google.common.collect.Maps;
import com.jxin.rpc.center.register.RegisterCenter;
import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.mark.MethodMark;
import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 代理中心上下文
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 15:34
 */
@Data
@Builder
public class CenterContext  {
    /**服务名*/
    private String applicationName;
    /**服务内所有服务*/
    private Map<String/*interfaceName*/, List<MethodMark>> serviceContext;
    /**服务上下文*/
    private RegisterCenter registerCenter;
    /**发信器容器*/
    private Map<URI, Sender> senderMap = Maps.newHashMap();

    /**
     * (函数式编程)
     * 如果key在容器中无值,则往<code>senderMap</code>中添加sender
     * 返回找到或者新生成的 {@link Sender}
     * @param  uri             map的key
     * @param  mappingFunction 要执行的方法
     * @return 消息发送器
     */
    public Sender computeIfAbsentFromSenderMap(URI uri,
                                               Function<URI, Sender> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        final Sender sender = this.senderMap.get(uri);
        if (sender != null) {
            return sender;
        }

        final Sender apply = mappingFunction.apply(uri);
        this.senderMap.put(uri, apply);
        return apply;
    }
}
