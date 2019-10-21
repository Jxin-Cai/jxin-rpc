package com.jxin.rpc.core.serializer;

/**
 * 数据的枚举类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 21:45
 */
public enum DataEnum {
    STR("字符串", 0),



    ;

    /**数据名称*/
    private final String name;
    /**数据类型*/
    private final Integer type;

    DataEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }
}
