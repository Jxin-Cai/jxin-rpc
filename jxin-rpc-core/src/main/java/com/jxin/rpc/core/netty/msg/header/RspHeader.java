package com.jxin.rpc.core.netty.msg.header;

import lombok.Builder;
import lombok.Data;

/**
 * 响应头
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:06
 */
@Data
@Builder
public class RspHeader {
    /**请求id*/
    private int requestId;
    /**协议版本*/
    private Integer version;
    /**请求类型*/
    private Integer type;
    /**响应code*/
    private Integer code;
    /**异常消息*/
    private String errMsg;
}
