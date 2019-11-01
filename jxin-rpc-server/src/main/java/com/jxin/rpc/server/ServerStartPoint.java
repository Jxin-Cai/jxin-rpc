package com.jxin.rpc.server;

import java.io.Closeable;

/**
 * 服务端启动类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/31 17:29
 */
public interface ServerStartPoint extends Closeable {
    /**
     * 启动服务
     * @param  clientPort 客户端端口号
     * @param  serverPort 服务端端口号
     */
    void startServer(int clientPort, int serverPort) throws InterruptedException;

}
