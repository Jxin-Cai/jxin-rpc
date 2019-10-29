package com.jxin.rpc.center.register.impl;

import com.google.common.collect.Lists;
import com.jxin.rpc.center.exc.RegisterCenterExc;
import com.jxin.rpc.center.register.RegisterCenter;
import com.jxin.rpc.core.util.serializer.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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
    private static final long LOOP_TIME = 10L;
    /**支持的协议列表*/
    private static final List<String> SCHEME_LIST = Lists.newArrayList("file");
    /**空的URI列表*/
    private static final List<URI> EMPTY_URI = Lists.newArrayList();
    /**应用配置*/
    private Properties appConfig = new Properties();
    /**配置文件地址*/
    private File configFile;
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
        configFile = new File(registerCenterUri);
        load();
    }

    /**
     * 加载配置
     * @throws RegisterCenterExc 加载数据异常
     * @author 蔡佳新
     */
    private void load(){
        synchronized (appConfig){
            try (final FileInputStream fi = new FileInputStream(configFile)){
                appConfig.load(fi);
            }catch (Exception e){
                throw new RegisterCenterExc(e);
            }
        }
    }



    @Override
    public void registerService(String application, URI uri){
        synchronized (appConfig){
            final String uriListStr = appConfig.getProperty(application);
            if(StringUtils.isBlank(uriListStr)){
                appConfig.setProperty(application, GsonUtil.GsonToStr(Lists.newArrayList(uri)));
                return;
            }
            final List<URI> uris = GsonUtil.GsonToList(uriListStr, URI.class);
            uris.add(uri);
            appConfig.setProperty(application, GsonUtil.GsonToStr(uris));
            try (final RandomAccessFile raf = new RandomAccessFile(configFile, "rw");
                 final FileChannel fileChannel = raf.getChannel()){

                final FileLock lock = fileChannel.lock();
                try (final FileOutputStream fo = new FileOutputStream(configFile)){
                    appConfig.store(fo, application);
                }finally {
                    lock.release();
                }
            }catch (Exception e){
                throw new RegisterCenterExc(e);
            }
        }
    }

    @Override
    public List<URI> getService(String application){
        final String uriListStr = appConfig.getProperty(application);
        if(StringUtils.isBlank(uriListStr)){
            load();
            return EMPTY_URI;
        }
        return GsonUtil.GsonToList(uriListStr, URI.class);
    }

    @Override
    public void close(){
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
