package com.jxin.rpc.core.consts;

import lombok.AllArgsConstructor;

/**
 * 响应状态枚举类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 14:54
 */
@AllArgsConstructor
public enum RspStatusEnum {
    RES_CODE_200("响应请求成功", 200),
    RES_CODE_404("服务生产者不存在", 404),
    RES_CODE_500("服务生产者执行异常", 500),
;


    /**名称*/
    private String name;
    /**code*/
    private Integer code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
