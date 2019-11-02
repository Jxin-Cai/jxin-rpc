package com.jxin.rpc.demo.server;

import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;
import com.jxin.rpc.server.ServerStartPoint;

import java.lang.reflect.InvocationTargetException;

/**
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/11/1 18:12
 */
public class ServerApplication {
    public static void main(String[] args) throws InterruptedException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ServerStartPoint serverStartPoint = ServiceLoaderUtil.load(ServerStartPoint.class);
        Runtime.getRuntime().addShutdownHook((Thread)serverStartPoint);
        serverStartPoint.startServer(4444, 8888);
    }
}
