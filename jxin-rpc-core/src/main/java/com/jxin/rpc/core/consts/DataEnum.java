package com.jxin.rpc.core.consts;

import lombok.AllArgsConstructor;

/**
 * 数据类型枚举类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 21:45
 */
@AllArgsConstructor
public enum DataEnum {
    STR("字符串", 0),

    ;

    /**数据名称*/
    private final String name;
    /**数据类型*/
    private final Integer type;

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }
}
