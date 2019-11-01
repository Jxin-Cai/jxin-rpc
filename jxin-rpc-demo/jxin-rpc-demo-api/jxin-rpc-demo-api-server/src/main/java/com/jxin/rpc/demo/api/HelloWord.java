package com.jxin.rpc.demo.api;

import com.jxin.rpc.core.inject.RegistService;

/**
 * hello word
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/11/1 18:16
 */
@RegistService
public interface HelloWord {
    String hello();
}
