package com.jxin.rpc.core.call.msg;

import com.jxin.rpc.core.call.msg.mark.MethodMark;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求消息
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 19:42
 */
@Data
@Builder
public class ReqMsg {
    /**服务标示*/
    private ServerMark serverMark;
    /**方法标识*/
    private MethodMark methodMark;
    /**参数列表*/
    private Object[] argArr;
}
