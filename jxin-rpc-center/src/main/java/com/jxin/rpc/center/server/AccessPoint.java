package com.jxin.rpc.center.server;

import com.jxin.rpc.center.register.RemoteService;

import java.io.Closeable;
import java.net.URI;
import java.util.List;

/**
 * 接入接口
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 20:40
 */
public interface AccessPoint extends Closeable {
    /**
     * 往 <code>CenterContext</code> 中添加远程服务转发feign实现
     * @param  remoteServices 远程服务实例列表
     * @param  serviceUri     注册中心URI
     * @author 蔡佳新
     */
    void addRemoteService(List<RemoteService> remoteServices, URI serviceUri);

    /**
     * 启动服务
     * @param  applicationName 服务名
     * @param  serviceUri      注册中心URI
     * @param  clientPort      客户端端口号
     * @param  serverPort      服务端端口号
     * @throws InterruptedException 服务启动异常
     * @author 蔡佳新
     */
    void startServer(String applicationName, URI serviceUri, int clientPort, int serverPort) throws InterruptedException;

}
