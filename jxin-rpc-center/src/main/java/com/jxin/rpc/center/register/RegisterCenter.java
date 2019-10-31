package com.jxin.rpc.center.register;

import com.jxin.rpc.center.exc.RegisterCenterExc;

import java.io.Closeable;
import java.net.URI;
import java.util.List;

/**
 * 注册中心
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/28 21:07
 */
public interface RegisterCenter extends Closeable {
    /**
     * 所有支持的协议
     * @return 协议列表
     * @author 蔡佳新
     */
    List<String> schemeList();

    /**
     * 连接注册中心
     * @param  registerCenterUri 注册中心地址
     * @throws RegisterCenterExc 协议不合法
     * @author 蔡佳新
     */
    void connect(URI registerCenterUri);
    /**
     * 注册服务
     * @param  application 服务名称
     * @param  uri         服务地址
     * @author 蔡佳新
     */
    void registerService(String application, URI uri);
    /**
     * 剔除注册的服务
     * @param  application 服务名称
     * @param  uri         服务地址
     * @author 蔡佳新
     */
    void removeService(String application, URI uri);

    /**
     * 查询服务地址
     * @param  application 服务名称
     * @return 服务地址
     * @author 蔡佳新
     */
    List<URI> getService(String application);
}
