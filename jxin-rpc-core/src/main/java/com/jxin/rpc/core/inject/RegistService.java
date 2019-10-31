package com.jxin.rpc.core.inject;

import java.lang.annotation.*;

/**
 * 需要被扫描的注册服务注解
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 20:21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface RegistService {
}
