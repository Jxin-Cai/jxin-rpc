package com.jxin.rpc.core.call.msg;

import com.jxin.rpc.core.mark.ReturnArgMark;
import lombok.Builder;
import lombok.Data;

/**
 * 响应消息
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/25 10:52
 */
@Data
@Builder
public class RspMsg {
    /**返回参数标识*/
    private ReturnArgMark returnArgMark;
    /**返回参数列表*/
    private Object returnArg;
}
