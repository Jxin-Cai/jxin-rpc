package com.jxin.rpc.core.call.msg.mark;

import lombok.Builder;
import lombok.Data;

/**
 * 返回参数标识
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/25 16:24
 */
@Data
@Builder
public class ReturnArgMark {
    /**字节码标示*/
    private String classMark;
    /**返回的数据是列表还是单体, true为是列表*/
    private boolean multi;
}
