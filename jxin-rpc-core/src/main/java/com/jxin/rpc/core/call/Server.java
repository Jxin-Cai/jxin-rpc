package com.jxin.rpc.core.call;

import java.io.Closeable;

/**
 * 服务端
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:05
 */
public interface Server extends Closeable {
    /**
     * 启动服务
     * @param  port            端口号
     * @throws InterruptedException 启动服务失败
     * @author 蔡佳新
     */
    void start(int port) throws InterruptedException;
}
