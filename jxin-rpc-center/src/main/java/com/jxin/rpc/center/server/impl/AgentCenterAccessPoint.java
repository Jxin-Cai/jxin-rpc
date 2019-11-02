package com.jxin.rpc.center.server.impl;

import com.google.common.collect.Lists;
import com.jxin.rpc.center.call.ForwordClient;
import com.jxin.rpc.center.exc.RegisterCenterExc;
import com.jxin.rpc.center.feign.ForwordFeign;
import com.jxin.rpc.center.register.RegisterCenter;
import com.jxin.rpc.center.register.RemoteService;
import com.jxin.rpc.center.server.AccessPoint;
import com.jxin.rpc.center.server.CenterContext;
import com.jxin.rpc.center.server.CenterContextSub;
import com.jxin.rpc.core.call.Client;
import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.Server;
import com.jxin.rpc.core.call.msg.MsgContext;
import com.jxin.rpc.core.call.msg.header.ReqHeader;
import com.jxin.rpc.core.call.msg.mark.MethodMark;
import com.jxin.rpc.core.call.msg.mark.RemoteServerMark;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.consts.ProVersionConsts;
import com.jxin.rpc.core.consts.ProviderEnum;
import com.jxin.rpc.core.feign.FeignFactory;
import com.jxin.rpc.core.server.hander.ProviderHander;
import com.jxin.rpc.core.util.IdUtil;
import com.jxin.rpc.core.util.serializer.SerializeUtil;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 代理中心接入实现
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 20:45
 */
@Slf4j
public class AgentCenterAccessPoint extends Thread implements AccessPoint {
    /**连接超时时间*/
    private static final long CONNECTION_TIMEOUT = 30000L;
    /**空的字节码数组*/
    private static final byte[] EMPTY_BYTE_ARR = new byte[0];
    /**host*/
    private static final String HOST = "localhost";
    /**请求转发客户端*/
    private static final ForwordClient FORWORD_CLIENT = ServiceLoaderUtil.load(ForwordClient.class);
    /**客户端*/
    private static final Client CLIENT =  ServiceLoaderUtil.load(Client.class);
    /**代理中心上下文*/
    private static final CenterContext CENTER_CONTEXT = CenterContext.builder().build();
    /**桩(装)的工厂类*/
    private static final FeignFactory FEIGN_FACTORY = ServiceLoaderUtil.load(FeignFactory.class);
    /**服务端*/
    private Server server = null;
    /**当前服务的注册中心*/
    private RegisterCenter registerCenter;
    /*
     * 继承Thread
     * 用于在程序关闭时释放资源。
     * @see java.lang.Thread#run()
     */
    public void run() {
        try {
            close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    //***********************************************addRemoteService***************************************************
    /**
     * 往 <code>CenterContext</code> 中添加远程服务转发feign实现
     * @param  remoteServices 远程服务实例列表
     * @param  serviceUri     注册中心URI
     * @author 蔡佳新
     */
    @Override
    public void addRemoteService(List<RemoteService> remoteServices, URI serviceUri) {
        final RegisterCenter serviceRegisterCenter = getRegisterCenter(serviceUri);
        try {
            remoteServices.forEach(remoteService ->{
                final List<URI> serviceUriList = serviceRegisterCenter.getService(remoteService.getApplicationName());
                remoteService.setServiceUriList(serviceUriList);
                assert CollectionUtils.isNotEmpty(serviceUriList) : "none register service : " + remoteService.getApplicationName();
                CENTER_CONTEXT.computeIfAbsentToApplicationFeignListMap(remoteService,
                                                                        this::createForwordFeignListToApp);
            });
        }finally {
            try {
                serviceRegisterCenter.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        /*控制服务端生成 桩*/
        createRemoteServiceFiegn(remoteServices);
    }

    //*************************************************startServer******************************************************
    /**
     * 启动服务
     * @param  applicationName 服务名
     * @param  serviceUri      注册中心URI
     * @param  clientPort      客户端端口号
     * @param  serverPort      服务端端口号
     * @throws InterruptedException 服务启动异常
     * @author 蔡佳新
     */
    @Override
    public void startServer(String applicationName, URI serviceUri, int clientPort, int serverPort) throws InterruptedException {
        // 注册服务
        CENTER_CONTEXT.setApplicationName(applicationName);
        registerCenter = getRegisterCenter(serviceUri);
        final URI uri = URI.create("rpc://" + HOST + ":" + clientPort);
        registerCenter.registerService(applicationName, uri);

        // 生成本地客户端请求桩
        final Sender localSender = createSender(uri, CLIENT);
        CENTER_CONTEXT.setLocalForwordFeign(
                FEIGN_FACTORY.createFeign(localSender,
                                          ForwordFeign.class,
                                          ServerMark.builder()
                                                    .application(CENTER_CONTEXT.getApplicationName())
                                                    .build())
        );

        // 拉取注册的服务
        CENTER_CONTEXT.setServiceContext(getRegisterService());
        // 为提供者注入上下文
        final CenterContextSub centerContextSub = (CenterContextSub)ProviderEnum.getByType(ProviderEnum.AGENT_PROVIDER.getType());
        centerContextSub.setCenterContext(CENTER_CONTEXT);

        // 开启服务
        if (server == null) {
            server = ServiceLoaderUtil.load(Server.class);
            server.start(serverPort);
        }
    }
    //***************************************private****startServer*****************************************************

    /**
     * 获取注册的服务列表
     * @return 注册的服务列表
     * @author 蔡佳新
     */
    private Map<String/*interfaceName*/, List<MethodMark>> getRegisterService() {
        final MsgContext reqMsgContext = MsgContext.builder()
                                                   .header(ReqHeader.builder()
                                                                    .requestId(IdUtil.getUUID())
                                                                    .version(ProVersionConsts.VERSION_1)
                                                                    .type(ProviderEnum.REGISTER_PROVIDER.getType())
                                                                    .build())
                                                   .body(EMPTY_BYTE_ARR)
                                                   .build();
        return CENTER_CONTEXT.getLocalForwordFeign()
                             .pullRegisterService(reqMsgContext);

    }

    //*************************************private****addRemoteService**************************************************
    private void createRemoteServiceFiegn(List<RemoteService> remoteServices){
        final List<ServerMark> remoteServerList = Lists.newArrayList();
        remoteServices.forEach(remoteService ->{
            remoteService.getServiceList()
                         .forEach(service -> remoteServerList.add(ServerMark.builder()
                                                                            .application(remoteService.getApplicationName())
                                                                            .interfaceName(service)
                                                                            .build()));
        });

        final MsgContext reqMsgContext = MsgContext.builder()
                                                   .header(ReqHeader.builder()
                                                                    .requestId(IdUtil.getUUID())
                                                                    .version(ProVersionConsts.VERSION_1)
                                                                    .type(ProviderEnum.REMOTE_FEIGN_PROVIDER.getType())
                                                                    .build())
                                                   .body(SerializeUtil.serialize(RemoteServerMark.builder()
                                                                                                 .remoteServerList(remoteServerList)
                                                                                                 .build()))
                                                   .build();
        CENTER_CONTEXT.getLocalForwordFeign().pushRemoteService(reqMsgContext);
    }
    /**
     * 创建服务的请求转发客户端 桩列表
     * @param  remoteService 订阅的远程服务实例
     * @return 消息发送器
     * @author 蔡佳新
     */
    private List<ForwordFeign> createForwordFeignListToApp(RemoteService remoteService) {
        return remoteService.getServiceUriList()
                            .stream()
                            .map(uri -> FEIGN_FACTORY.createFeign(putToSenderMap(uri),
                                        ForwordFeign.class,
                                        ServerMark.builder()
                                                  .application(remoteService.getApplicationName())
                                                  .interfaceName(ForwordFeign.class.getName())
                                                  .build()))
                            .collect(Collectors.toList());
    }
    /**
     * 如果key在容器中无值,则往<code>senderMap</code>中添加sender
     * 返回找到或者新生成的 {@link Sender}
     * @param  uri             map的key
     * @return 消息发送器
     */
    private Sender putToSenderMap(URI uri) {
        return CENTER_CONTEXT.computeIfAbsentToSenderMap(uri, this::createSenderToRemote);
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

    //**************************************private****getLocalForwordFeign*********************************************
    /**
     * 获取注册中心的实现
     * @param  serviceUri 注册中心URI
     * @throws RegisterCenterExc 获取不到该协议的注册中心实现
     * @return 注册中心的实现
     * @author 蔡佳新
     */
    private RegisterCenter getRegisterCenter(URI serviceUri) {
        final Collection<RegisterCenter> registerCenterList = ServiceLoaderUtil.loadAll(RegisterCenter.class);
        for (RegisterCenter registerCenter : registerCenterList) {
            if(registerCenter.schemeList().contains(serviceUri.getScheme())) {
                registerCenter.connect(serviceUri);
                return registerCenter;
            }
        }
        throw new RegisterCenterExc("Unlawfulness scheme!");
    }
    @Override
    public void close() throws IOException {
        if(server != null) {
            server.close();
        }
        if (registerCenter != null) {
            registerCenter.close();
        }
        FORWORD_CLIENT.close();
        CLIENT.close();
    }
}
