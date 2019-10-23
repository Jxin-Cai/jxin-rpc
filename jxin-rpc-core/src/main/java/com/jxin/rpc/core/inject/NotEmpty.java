package com.jxin.rpc.core.inject;

import java.lang.annotation.*;

/**
 * 集合不能为空
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 21:21
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotEmpty {
}
