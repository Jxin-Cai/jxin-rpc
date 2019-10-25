package com.jxin.rpc.server.call;

/**
 * 服务端
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 16:05
 */
public interface Server {
    void start(int port) throws Exception;
    void stop();
}
