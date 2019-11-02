package com.jxin.rpc.core.inject;

import java.lang.annotation.*;

/**
 * 注入远程服务的注解
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 20:21
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
}
