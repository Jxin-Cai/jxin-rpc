package com.jxin.rpc.core.call.msg.mark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 注册的服务标识
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/11/1 11:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterServerMark {
    /**注册的服务容器*/
    private Map<String/*interfaceName*/, List<MethodMark>> registerServiceMap;

}
