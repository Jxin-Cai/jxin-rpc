package com.jxin.rpc.core.consts;

import com.google.common.collect.Maps;
import com.jxin.rpc.core.call.msg.hander.ReqMsgHander;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.HashMap;

/**
 * 请求类型枚举类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:30
 */
@AllArgsConstructor
public enum ReqEnum {
    CLIENT_REQ("客户端请求", 0),
    AGENT_REQ("代理端请求", 1),
    ;

    /**请求名称*/
    private final String name;
    /**请求类型*/
    private final Integer type;

    /**根据type获取ReqMsgHander的map*/
    private static final HashMap<Integer/*type*/, ReqMsgHander> TYPE_MAP = Maps.newHashMap();

    static {
        // reqMsgHander init
        final Collection<ReqMsgHander> reqMsgHanderList = ServiceLoaderUtil.loadAll(ReqMsgHander.class);
        for (ReqMsgHander reqMsgHander : reqMsgHanderList) {
            TYPE_MAP.put(reqMsgHander.type(), reqMsgHander);
        }
    }

    /**
     * 获取请求消息处理器实例,根据type
     * @param  type 请求类型
     * @return 消息处理器
     * @author 蔡佳新
     */
    public static ReqMsgHander getByType(Integer type){
        return TYPE_MAP.get(type);
    }




    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }
}
