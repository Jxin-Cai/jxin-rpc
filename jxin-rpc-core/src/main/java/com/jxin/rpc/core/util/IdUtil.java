package com.jxin.rpc.core.util;

import java.util.UUID;

/**
 * Id生成器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/25 20:18
 */
public interface IdUtil {

    /**
     * 获取随机UUID
     * @return 随机UUID
     * @author 蔡佳新
     */
     static String getUUID(){
        return UUID.randomUUID().toString();
    }
}
