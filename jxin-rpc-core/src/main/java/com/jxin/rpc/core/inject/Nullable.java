package com.jxin.rpc.core.inject;

import java.lang.annotation.*;

/**
 * 可以为null的注解
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 21:21
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Nullable {
}
