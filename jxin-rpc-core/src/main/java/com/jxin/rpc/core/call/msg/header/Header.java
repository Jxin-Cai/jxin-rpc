package com.jxin.rpc.core.call.msg.header;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * @author 蔡佳新
 * @version 1.0
 * @since jdk 1.8
 */
@Data
@AllArgsConstructor
public class Header {
    /**请求id*/
    private final String requestId;
    /**协议版本*/
    private final Integer version;
    /**提供者类型*/
    private Integer providerType;

    /**
     * 获取当前实体bean属性值所占空间之和
     * @return 实体bean所有属性所占的空间
     * @author 蔡佳新
     */
    public int length() {
        /* version.size() + getProviderType.size() + requestId.length + requestId.size()*/
        return Integer.BYTES * 3 + getRequestIdLen();
    }

    /**
     * 获得请求id的长度
     * @return 请求id的长度
     * @author 蔡佳新
     */
    public int getRequestIdLen(){
        return requestId.getBytes(StandardCharsets.UTF_8).length;
    }
}
