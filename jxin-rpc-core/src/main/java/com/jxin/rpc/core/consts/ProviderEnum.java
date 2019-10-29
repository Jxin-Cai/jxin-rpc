package com.jxin.rpc.core.consts;

import com.google.common.collect.Maps;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;
import com.jxin.rpc.core.server.hander.ProviderHander;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.HashMap;

/**
 * 服务提供者枚举类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:30
 */
@AllArgsConstructor
public enum ProviderEnum {
    /**代理端提供者*/
    AGENT_PROVIDER("代理端提供者", 0),
    /**服务端提供者*/
    SERVER_PROVIDER("服务端提供者", 1),
    ;

    /**请求名称*/
    private final String name;
    /**请求类型*/
    private final Integer type;

    /**根据type获取ProviderHander的map*/
    private static final HashMap<Integer/*type*/, ProviderHander> TYPE_MAP = Maps.newHashMap();

    static {
        // ProviderHander init
        final Collection<ProviderHander> providerHanderList = ServiceLoaderUtil.loadAll(ProviderHander.class);
        for (ProviderHander providerHander : providerHanderList) {
            TYPE_MAP.put(providerHander.type(), providerHander);
        }
    }

    /**
     * 获取请求消息处理器实例,根据type
     * @param  type 请求类型
     * @return 消息处理器
     * @author 蔡佳新
     */
    public static ProviderHander getByType(Integer type){
        return TYPE_MAP.get(type);
    }




    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }
}
