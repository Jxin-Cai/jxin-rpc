package com.jxin.rpc.core.call.msg.header;

import lombok.Builder;
import lombok.Data;

/**
 * 请求头
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:06
 */
@Builder
public class ReqHeader extends Header{

    /**
     * 获取当前实体bean属性值所占空间之和
     * @return 实体bean所有属性所占的空间
     * @author 蔡佳新
     */
    @Override
    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES;
    }
}
