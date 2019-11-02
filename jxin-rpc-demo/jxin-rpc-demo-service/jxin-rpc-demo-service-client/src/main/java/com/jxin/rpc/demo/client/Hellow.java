package com.jxin.rpc.demo.client;

import com.jxin.rpc.core.inject.Autowired;
import com.jxin.rpc.core.inject.Service;
import com.jxin.rpc.demo.api.HelloWord;

/**
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/11/2 16:15
 */
@Service
public class Hellow {
    @Autowired
    private HelloWord helloWord;

    public void hell(){
        System.out.println(helloWord.hello());
    }
}
