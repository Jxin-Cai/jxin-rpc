package com.jxin.rpc.center.server.impl;

import com.jxin.rpc.center.call.ForwordClient;
import com.jxin.rpc.center.exc.RegisterCenterExc;
import com.jxin.rpc.center.server.AccessPoint;
import com.jxin.rpc.center.server.CenterContext;
import com.jxin.rpc.core.call.Client;
import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.feign.FeignFactory;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * 代理中心接入实现
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 20:45
 */
public class AgentCenterAccessPoint implements AccessPoint {
    private static final long CONNECTION_TIMEOUT = 30000L;
    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private static final URI CENTER_URI = URI.create("rpc://" + HOST + ":" + PORT);
    private final Sender LOCAL_SENDER = createSender(CENTER_URI, CLIENT);

    private static final ForwordClient FORWORD_CLIENT = ServiceLoaderUtil.load(ForwordClient.class);
    private static final Client CLIENT = ServiceLoaderUtil.load(Client.class);
    private static final CenterContext CENTER_CONTEXT = CenterContext.builder().build();
    private static final FeignFactory FEIGN_FACTORY = ServiceLoaderUtil.load(FeignFactory.class);
    //***********************************************addRemoteService***************************************************
    /**
     * TODO 代理端的客户端生成得改写下,不需要具体到接口
     * 客户端获取远程服务的引用
     * @param  uri 远程服务地址
     * @param  serviceClass 服务的接口类的Class
     * @param  <T> 服务接口的类型
     * @return 远程服务引用
     */
    @Override
    public <T> T addRemoteService(URI uri, Class<T> serviceClass) {
        final Sender sender = computeIfAbsentFromSenderMap(uri);
        return FEIGN_FACTORY.createFeign(sender,
                                         serviceClass,
                                         ServerMark.builder()
                                                   .application(CENTER_CONTEXT.getApplicationName())
                                                   .interfaceName(serviceClass.getName())
                                                   .build());
    }
    //***********************************************getLocalSender***************************************************
    @Override
    public Sender getLocalSender() {
        return LOCAL_SENDER;
    }


    //***********************************************setApplicationName*************************************************
    /**
     * 设置服务名
     * @param  applicationName 服务名
     * @author 蔡佳新
     */
    @Override
    public void setApplicationName(String applicationName) {
        CENTER_CONTEXT.setApplicationName(applicationName);
    }
    //*************************************private****addRemoteService**************************************************
    /**
     * 如果key在容器中无值,则往<code>senderMap</code>中添加sender
     * 返回找到或者新生成的 {@link Sender}
     * @param  uri             map的key
     * @return 消息发送器
     */
    private Sender computeIfAbsentFromSenderMap(URI uri) {
        return CENTER_CONTEXT.computeIfAbsentFromSenderMap(uri, this::createSenderToRemote);
    }
    /**
     * 创建转发远端请求的消息发送器
     * @param  uri    地址
     * @return 消息发送器
     * @author 蔡佳新
     */
    private Sender createSenderToRemote(URI uri) {
        return createSender(uri, FORWORD_CLIENT);
    }
    /**
     * 创建消息发送器
     * @param  uri    地址
     * @param  client 客户端
     * @return 消息发送器
     * @author 蔡佳新
     */
    private Sender createSender(URI uri, Client client) {
        try {
            return client.createSender(new InetSocketAddress(uri.getHost(), uri.getPort()), CONNECTION_TIMEOUT);
        } catch (Exception e) {
            throw new RegisterCenterExc(e);
        }
    }
}
