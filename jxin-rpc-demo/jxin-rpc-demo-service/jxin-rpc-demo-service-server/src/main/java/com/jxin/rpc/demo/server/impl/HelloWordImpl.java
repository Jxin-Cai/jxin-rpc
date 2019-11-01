package com.jxin.rpc.demo.server.impl;

import com.jxin.rpc.core.inject.Service;
import com.jxin.rpc.demo.api.HelloWord;

/**
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/11/1 18:24
 */
@Service
public class HelloWordImpl implements HelloWord {
    @Override
    public String hello() {
        return "say hell!";
    }
}
