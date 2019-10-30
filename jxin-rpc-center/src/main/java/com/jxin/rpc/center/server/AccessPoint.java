package com.jxin.rpc.center.server;

import com.jxin.rpc.center.exc.RegisterCenterExc;
import com.jxin.rpc.center.feign.ForwordFeign;
import com.jxin.rpc.center.register.RegisterCenter;
import com.jxin.rpc.center.register.RemoteService;
import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;

import java.io.Closeable;
import java.net.URI;
import java.util.Collection;
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
     * 获得本地请求跳转 桩
     * @return 本地请求跳转 桩
     * @author 蔡佳新
     */
    ForwordFeign getLocalForwordFeign();

    /**
     * 启动服务
     * @param  applicationName 服务名
     * @param  clientPort      客户端端口号
     * @param  serverPort      服务端端口号
     * @throws InterruptedException 服务启动异常
     * @author 蔡佳新
     */
    void startServer(String applicationName, int clientPort, int serverPort) throws InterruptedException;
    /**
     * 获取注册中心的实现
     * @param  serviceUri 注册中心URI
     * @throws RegisterCenterExc 获取不到该协议的注册中心实现
     * @return 注册中心的实现
     * @author 蔡佳新
     */
    default RegisterCenter getRegisterCenter(URI serviceUri) {
        final Collection<RegisterCenter> registerCenterList = ServiceLoaderUtil.loadAll(RegisterCenter.class);
        for (RegisterCenter registerCenter : registerCenterList) {
            if(registerCenter.schemeList().contains(serviceUri.getScheme())) {
                registerCenter.connect(serviceUri);
                return registerCenter;
            }
        }
        throw new RegisterCenterExc("Unlawfulness scheme!");
    }


}
