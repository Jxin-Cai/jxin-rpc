package com.jxin.rpc.center.register.impl;

import com.google.common.collect.Lists;
import com.jxin.rpc.center.exc.RegisterCenterExc;
import com.jxin.rpc.center.register.RegisterCenter;
import com.jxin.rpc.core.util.serializer.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * (仅用于测试)
 * 本地文件注册中心
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/28 21:12
 */
public class LocalFileRegisterCenter implements RegisterCenter, Closeable {
    /**定时任务的调用间隔*/
    protected static final long LOOP_TIME = 10L;
    /**支持的协议列表*/
    private static final List<String> SCHEME_LIST = Lists.newArrayList("file");
    private static final List<URI> EMPTY_URI = Lists.newArrayList();
    /**配置文件*/
    private Properties config = new Properties();
    /**配置文件地址*/
    private String configFilePath = null;
    /**定时任务执行线程池*/
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    /**定时任务的 Future对象*/
    private final ScheduledFuture scheduledFuture;
    /**
     * 在构造器中开启清理超时RspFuture的定时job
     * @author 蔡佳新
     */
    public LocalFileRegisterCenter() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::load,
                                                                       0,
                                                                       LOOP_TIME,
                                                                       TimeUnit.SECONDS);
    }
    @Override
    public List<String> schemeList() {
        return SCHEME_LIST;
    }

    @Override
    public void connect(URI registerCenterUri) {
        if(!SCHEME_LIST.contains(registerCenterUri.getScheme())){
            throw new RegisterCenterExc("Unlawfulness scheme!");
        }

        final String path = getPath();
        configFilePath = path + registerCenterUri.getPath();
        load();
    }

    /**
     * 加载配置
     * @throws RegisterCenterExc 加载数据异常
     * @author 蔡佳新
     */
    private void load(){
        synchronized (config){
            try (final FileInputStream fi = new FileInputStream(configFilePath)){
                config.load(fi);
            }catch (Exception e){
                throw new RegisterCenterExc(e);
            }
        }
    }

    /**
     * 根据不同系统获取不同路径
     * @throws RegisterCenterExc 不合法的系统类型
     * @return 路径
     */
    private String getPath() {
        if(SystemUtils.IS_OS_WINDOWS){
            return "E:\\tmp\\";
        }
        if(SystemUtils.IS_OS_LINUX){
            return "/tmp";
        }
        throw new RegisterCenterExc("Unlawfulness os");
    }

    @Override
    public void registerService(String application, URI uri){
        synchronized (config){
            final String uriListStr = config.getProperty(application);
            if(StringUtils.isBlank(uriListStr)){
                config.setProperty(application, GsonUtil.GsonToStr(Lists.newArrayList(uri)));
                return;
            }
            final List<URI> uris = GsonUtil.GsonToList(uriListStr, URI.class);
            uris.add(uri);
            config.setProperty(application, GsonUtil.GsonToStr(uris));

            try (final FileOutputStream fo = new FileOutputStream(configFilePath)){
                config.store(fo, application);
            }catch (Exception e){
                throw new RegisterCenterExc(e);
            }
        }
    }

    @Override
    public List<URI> getService(String application) throws IOException {
        final String uriListStr = config.getProperty(application);
        if(StringUtils.isBlank(uriListStr)){
            load();
            return EMPTY_URI;
        }
        return GsonUtil.GsonToList(uriListStr, URI.class);
    }

    @Override
    public void close() throws IOException {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
