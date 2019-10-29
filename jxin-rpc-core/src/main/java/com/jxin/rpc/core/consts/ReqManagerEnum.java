package com.jxin.rpc.core.consts;

import com.google.common.collect.Maps;
import com.jxin.rpc.core.call.msg.manage.ReqManager;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.HashMap;

/**
 * 請求管理器枚举类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 14:38
 */
@AllArgsConstructor
public enum ReqManagerEnum {
    /**代理端提供者*/
    AGENT_REQ_MANAGER("代理端请求管理器", 0),
    /**服务端提供者*/
    CLIENT_REQ_MANAGER("客户端请求管理器", 1),
    ;

    /**请求管理器名称*/
    private final String name;
    /**请求管理器类型*/
    private final Integer type;

    /**根据type获取ProviderHander的map*/
    private static final HashMap<Integer/*type*/, ReqManager> TYPE_MAP = Maps.newHashMap();

    static {
        // ProviderHander init
        final Collection<ReqManager> reqManagerList = ServiceLoaderUtil.loadAll(ReqManager.class);
        for (ReqManager reqManager : reqManagerList) {
            TYPE_MAP.put(reqManager.type(), reqManager);
        }
    }

    /**
     * 获取请求管理器,根据type
     * @param  type 请求管理器类型
     * @return 请求管理器
     * @author 蔡佳新
     */
    public static ReqManager getByType(Integer type){
        return TYPE_MAP.get(type);
    }




    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }
}
