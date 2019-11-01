package com.jxin.rpc.center;

import com.google.common.collect.Lists;
import com.jxin.rpc.center.exc.RegisterCenterExc;
import com.jxin.rpc.center.register.RemoteService;
import com.jxin.rpc.center.server.AccessPoint;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.List;

/**
 * 中心启动类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 18:03
 */
@Slf4j
public class CenterApplication {


    public static void main(String[] args) {
        AccessPoint accessPoint = null;
        try {
            accessPoint = ServiceLoaderUtil.load(AccessPoint.class);
            Runtime.getRuntime().addShutdownHook((Thread)accessPoint);
            final File file = new File(getPath() + "application.properties");
            accessPoint.startServer("jxin-client", file.toURI(), 9999, 5555);
            //accessPoint.startServer("jxin-service", file.toURI(), 8888, 4444);

            final List<RemoteService> remoteServices = mockRemoteServiceList();
            // 中添加远程服务转发feign实现
            accessPoint.addRemoteService(remoteServices, file.toURI());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            if(accessPoint != null){
                ((Thread)accessPoint).run();
            }
           // System.exit();
        }
    }

    /**
     * mock个远程服务列表(没时间不纠结了)
     * @return 订阅的远程服务列表
     * @author 蔡佳新
     */
    private static List<RemoteService> mockRemoteServiceList(){
        final List<RemoteService> result = Lists.newArrayList();
        final RemoteService remoteService = RemoteService.builder()
                                                         .applicationName("jxin-service")
                                                         .serviceList(Lists.newArrayList("com.jxin.rpc.demo.api.HelloWord"))
                                                         .build();
        result.add(remoteService);
        return result;
    }

    /**
     * 根据不同系统获取不同路径
     * @throws RegisterCenterExc 不合法的系统类型
     * @return 路径
     */
    private static String getPath() {
        if(SystemUtils.IS_OS_WINDOWS){
            return "E:\\tmp\\";
        }
        if(SystemUtils.IS_OS_LINUX){
            return "/tmp/";
        }
        throw new RegisterCenterExc("Unlawfulness os");
    }
}
