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
    /**文件名*/
    private static final String REGISTRATION_CENTER = "Registration Center";
    /**应用配置*/
    private final Properties appConfig = new Properties();
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
        if(!configFile.exists()){
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                throw new RegisterCenterExc(e);
            }
        }
        load();
    }

    @Override
    public void registerService(String application, URI uri){
        synchronized (appConfig){
            final boolean needStore = addServiceUri(application, uri);
            if(needStore){
                store();
            }
        }
    }

    @Override
    public void removeService(String application, URI uri) {
        synchronized (appConfig){
            removeServiceUri(application, uri);
            store();
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

    /**
     * 往注册中心添加该服务的uri
     * @param  application 服务名
     * @param  uri         uri
     * @return 如果配置有变更则返回 true,需要序列化到文件
     * @author 蔡佳新
     */
    private boolean addServiceUri(String application, URI uri) {
        final String uriListStr = appConfig.getProperty(application);
        if(StringUtils.isBlank(uriListStr)){
            appConfig.setProperty(application, GsonUtil.GsonToStr(Lists.newArrayList(uri)));
            return true;
        }
        final List<URI> uris = GsonUtil.GsonToList(uriListStr, URI.class);
        if(uris.contains(uri)){
            return false;

        }
        uris.add(uri);
        appConfig.setProperty(application, GsonUtil.GsonToStr(uris));
        return true;
    }
    /**
     * 往注册中心删除该服务的uri
     * @param  application 服务名
     * @param  uri         uri
     * @author 蔡佳新
     */
    private void removeServiceUri(String application, URI uri) {
        final String uriListStr = appConfig.getProperty(application);
        if(StringUtils.isBlank(uriListStr)){
            return;
        }
        final List<URI> uris = GsonUtil.GsonToList(uriListStr, URI.class);
        uris.remove(uri);
        appConfig.setProperty(application, GsonUtil.GsonToStr(uris));
    }
    //******************************************private***common*******************************************************
    /**
     * 序列化配置到文件
     * @author 蔡佳新
     */
    private void store() {
        try (final RandomAccessFile raf = new RandomAccessFile(configFile, "rw");
             final FileChannel fileChannel = raf.getChannel()) {
            // final FileLock lock = fileChannel.lock();
            try (final FileOutputStream fo = new FileOutputStream(configFile)) {
                appConfig.store(fo, REGISTRATION_CENTER);
            } finally {
                // lock.release();
            }
        } catch (Exception e) {
            throw new RegisterCenterExc(e);
        }
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
    public void close(){
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
