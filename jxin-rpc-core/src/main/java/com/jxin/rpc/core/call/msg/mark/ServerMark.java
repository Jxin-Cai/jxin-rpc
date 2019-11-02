package com.jxin.rpc.core.call.msg.mark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务标识
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 19:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerMark {
    /**服务名*/
    private String application;
    /**接口全路径名*/
    private String interfaceName;


}
