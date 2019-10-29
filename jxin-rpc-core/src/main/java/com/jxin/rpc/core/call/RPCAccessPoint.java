package com.jxin.rpc.core.call;

import java.io.Closeable;
import java.net.URI;

/**
 * 远程调用接入接口
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 10:46
 */
public interface RPCAccessPoint extends Closeable {
    /**
     * 客户端获取远程服务的引用
     * @param  uri 远程服务地址
     * @param  serviceClass 服务的接口类的Class
     * @param  <T> 服务接口的类型
     * @return 远程服务引用
     */
    <T> T addRemoteService(URI uri, Class<T> serviceClass);

    /**
     * 注册服务的实现实例
     * @param  service 实现实例
     * @param  serviceClass 服务的接口类的Class
     * @param  <T> 服务接口的类型
     * @return 服务地址
     */
    <T> URI registerServiceProvider(T service, Class<T> serviceClass);

    /**
     * 服务端启动RPC框架，监听接口，开始提供远程服务。
     * @return 服务实例，用于程序停止的时候安全关闭服务。
     */
    Closeable startServer();
}
