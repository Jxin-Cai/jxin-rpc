package com.jxin.rpc.demo.client;

import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;
import com.jxin.rpc.server.ServerStartPoint;

import java.io.IOException;

/**
 * 中心启动类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 18:03
 */
public class ClientApplication {


    public static void main(String[] args) throws InterruptedException {
        final ServerStartPoint serverStartPoint = ServiceLoaderUtil.load(ServerStartPoint.class);
        Runtime.getRuntime().addShutdownHook((Thread)serverStartPoint);
        serverStartPoint.startServer(5555, 9999);
    }


}
