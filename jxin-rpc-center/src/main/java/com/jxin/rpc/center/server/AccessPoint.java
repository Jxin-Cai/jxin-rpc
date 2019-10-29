package com.jxin.rpc.center.server;

import com.jxin.rpc.center.exc.RegisterCenterExc;
import com.jxin.rpc.center.register.RegisterCenter;
import com.jxin.rpc.core.call.Client;
import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;

import java.net.URI;
import java.util.Collection;

/**
 * 接入接口
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 20:40
 */
public interface AccessPoint {
    /**
     * 客户端获取远程服务的引用
     * @param  uri 远程服务地址
     * @param  serviceClass 服务的接口类的Class
     * @param  <T> 服务接口的类型
     * @return 远程服务引用
     */
    <T> T addRemoteService(URI uri, Class<T> serviceClass);
    /**
     * 获得本地消息发送器
     * @return 本地消息发送器
     * @author 蔡佳新
     */
    Sender getLocalSender();

    /**
     * 设置服务名
     * @param  applicationName 服务名
     * @author 蔡佳新
     */
    void setApplicationName(String applicationName);
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
