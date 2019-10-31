package com.jxin.rpc.demo.client;

import com.jxin.rpc.core.call.Server;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;

/**
 * 中心启动类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 18:03
 */
public class ClientApplication {


    public static void main(String[] args) throws InterruptedException {
        final Server server = ServiceLoaderUtil.load(Server.class);
        server.start(9999);
    }


}
