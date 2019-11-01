package com.jxin.rpc.server.start;

import com.jxin.rpc.core.call.Server;
import com.jxin.rpc.core.consts.ProviderEnum;
import com.jxin.rpc.core.server.hander.ProviderHander;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;
import com.jxin.rpc.server.ServerStartPoint;
import com.jxin.rpc.server.scan.ApplicationContext;
import com.jxin.rpc.server.scan.ApplicationContextSub;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;

/**
 * 本地启动类,不依赖外部框架
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/31 17:52
 */
@Slf4j
public class LocalServerStartPoint extends Thread implements ServerStartPoint {

    /**host*/
    private static final String HOST = "localhost";
    private static final String PKG = "";
    /**服务端*/
    private Server server = null;
    /**应用服务上下文*/
    private ApplicationContext applicationContext;

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
    @Override
    public void startServer(int clientPort, int serverPort) throws InterruptedException {
        applicationContext = new ApplicationContext(PKG, URI.create("rpc://" + HOST + ":" + clientPort));
        // 服务端提供者
        initProviderHander(ProviderEnum.SERVER_PROVIDER);
        // 服务端注册服务提供者
        initProviderHander(ProviderEnum.REGISTER_PROVIDER);
        // 开启服务
        if (server == null) {
            server = ServiceLoaderUtil.load(Server.class);
            server.start(serverPort);
        }
    }
    /**
     * 初始化服务提供者
     * @param  providerEnum       服务提供者枚举类
     * @author 蔡佳新
     */
    private void initProviderHander( ProviderEnum providerEnum) {
        final ProviderHander providerHander = ProviderEnum.getByType(providerEnum.getType());
        ((ApplicationContextSub) providerHander).setApplicationContext(applicationContext);
    }

    @Override
    public void close() throws IOException {
        if(server != null) {
            server.close();
        }
        if(applicationContext != null){
            applicationContext.close();
        }

    }
}
