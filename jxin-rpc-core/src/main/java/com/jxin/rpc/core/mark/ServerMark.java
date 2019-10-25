package com.jxin.rpc.core.mark;

import lombok.Builder;
import lombok.Data;

/**
 * 服务标识
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 19:32
 */
@Data
@Builder
public class ServerMark {
    /**服务名*/
    private final String application;
    /**接口全路径名*/
    private final String interfaceName;


}
