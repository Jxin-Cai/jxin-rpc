package com.jxin.rpc.server.scan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jxin.rpc.core.call.Client;
import com.jxin.rpc.core.call.Sender;
import com.jxin.rpc.core.call.msg.mark.MethodMark;
import com.jxin.rpc.core.call.msg.mark.ServerMark;
import com.jxin.rpc.core.exc.InitFeignExc;
import com.jxin.rpc.core.exc.ScanExc;
import com.jxin.rpc.core.feign.FeignFactory;
import com.jxin.rpc.core.inject.Autowired;
import com.jxin.rpc.core.inject.RegistService;
import com.jxin.rpc.core.inject.Service;
import com.jxin.rpc.core.util.spi.ServiceLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 应用服务上下文
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/24 15:59
 */
@Slf4j
public class ApplicationContext implements Closeable{
    /**连接超时时间*/
    private static final long CONNECTION_TIMEOUT = 30000L;

    /**全部服务上下文*/
    private Map<String/*serviceName*/, List<Object>/*serviceImplList*/> serviceContext = Maps.newHashMap();
    /**注册服务上下文*/
    private Map<String/*interfaceName*/, Object/*serviceImpl*/> registServiceContext = Maps.newHashMap();
    /**注册服务方法上下文*/
    private Map<String/*interfaceName*/, Map<MethodMark, Method>/*methodMap*/> registMethodContext = Maps.newHashMap();
    /**远程服务的feign客户端*/
    private Map<String/*interfaceName*/, Object/*interfaceFeign*/> remoteServiceContext;

    /**桩(装)的工厂类*/
    private static final FeignFactory FEIGN_FACTORY = ServiceLoaderUtil.load(FeignFactory.class);
    /**客户端*/
    private Client client;
    /**本地请求代理端 消息发送器*/
    private Sender localSender;
    /**代理端的uri*/
    private URI agentUri;

    public ApplicationContext() {
       super();
    }
    public ApplicationContext(String pkg, URI agentUri) {
        this.agentUri = agentUri;
        // 扫包
        scanPackage(pkg);
        // 注入服务上下文
        injectServiceContext();
        // 往methodContext中添加 所有服务实例的所有方法
        putAllMethod();
        client = ServiceLoaderUtil.load(Client.class);
    }

    /**
     * 根据接口类全路径名 获取服务名
     * @param  interfaceName 接口类全路径名
     * @return 实现类
     * @author 蔡佳新
     */
    public Object getRegistServiceList(String interfaceName) {
        return registServiceContext.get(interfaceName);
    }
    /**
     * 根据实现类名 获取方法Map
     * @param  interfaceName 接口名
     * @return 实现类列表
     * @author 蔡佳新
     */
    public Map<MethodMark, Method> getRegistMethod(String interfaceName) {
        return registMethodContext.get(interfaceName);
    }

    /**
     * 获取所有注册的服务
     * @return 所有注册的服务
     * @author 蔡佳新
     */
    public Map<String/*interfaceName*/, List<MethodMark>> getAllService(){
        final Map<String/*interfaceName*/, List<MethodMark>> result = new HashMap<>(registMethodContext.size());
        registMethodContext.forEach((k,v) -> result.put(k, Lists.newArrayList(v.keySet())));
        return result;
    }

    /**
     * 注入远程服务
     * @param  serverMarkList 服务标识；列表
     * @author 蔡佳新
     */
    public void injectRemoteService(List<ServerMark> serverMarkList) throws TimeoutException, InterruptedException {
        if(localSender == null){
            localSender = client.createSender(new InetSocketAddress(agentUri.getHost(), agentUri.getPort()),
                                              CONNECTION_TIMEOUT);
        }

        this.remoteServiceContext = new HashMap<>(serverMarkList.size());
        serverMarkList.forEach(serverMark -> {
            try {
                final Class<?> interfaceClass = Class.forName(serverMark.getInterfaceName());
                final Object feign = FEIGN_FACTORY.createFeign(localSender, interfaceClass, serverMark);
                remoteServiceContext.put(interfaceClass.getName(), feign);
            } catch (ClassNotFoundException e) {
                throw new InitFeignExc(e);
            }
        });
        serviceContext.values().forEach(this::injectToService);
    }

    //***********************************************injectRemoteService************************************************
    /**
     * 为本地服务注入远程服务
     * @param  serviceList 服务列表
     * @author 蔡佳新
     */
    private void injectToService(List<Object> serviceList) {
        serviceList.forEach(obj -> {
            final Field[] declaredFields = obj.getClass().getDeclaredFields();
            if(ArrayUtils.isEmpty(declaredFields)){
                return;
            }
            Arrays.stream(declaredFields).forEach(field -> {
                final Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
                if(ArrayUtils.isEmpty(declaredAnnotations)){
                    return;
                }
                final boolean needInject = Arrays.stream(declaredAnnotations).anyMatch(this::isAutowired);
                if(!needInject){
                    return;
                }
                final Object remoteService = remoteServiceContext.get(field.getDeclaringClass().getName());
                try {
                    field.set(obj, remoteService);
                } catch (IllegalAccessException e) {
                    throw new InitFeignExc(e);
                }
            });
        });
    }

    /**
     * 判断是不是 注入远程服务的注解
     * @param  annotation 注解
     * @return 如果是注入远程服务的注解返回 <code>true</code>
     * @author 蔡佳新
     */
    private boolean isAutowired(Annotation annotation){
        return annotation instanceof Autowired;
    }
    //***********************************************scanPackage********************************************************
    /**
     * 扫包生成服务,并往<code>registServiceContext</code>和<code>serviceContext</code>注入服务
     * @param  pkg 包路径
     * @author 蔡佳新
     */
    private void scanPackage(final String pkg){
        final String pkgDirPath = pkg.replaceAll("\\.", "/");
        final URL url = getClass().getClassLoader().getResource(pkgDirPath);
        if(url == null){
            return;
        }
        final File pkgDir = new File(url.getFile());
        final File[] fs = pkgDir.listFiles(file -> {
            // 文件夹过滤掉,递归进去扫
            if(file.isDirectory()){
                scanPackage(pkg + "." + file.getName());
                return false;
            }
            // 保留后缀.class 的文件
            return file.getName().endsWith(".class");
        });
        if(ArrayUtils.isEmpty(fs)){
            return;
        }
        Arrays.stream(fs).map(file ->{
            // 去除.class以后的文件名
            final int cutPoint = file.getName().lastIndexOf('.');
            final String filename = file.getName().substring(0, cutPoint);
            // 构建一个类全名(包名.类名)
            final String clazzName = pkg + "." + filename;
            // 反射构建对象
            try {
                return Class.forName(clazzName).newInstance();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }).filter(obj -> !withoutPut(obj))
          .forEach(this::putService);
    }
    /**
     * 将对象放到map容器
     * @param  obj 对象
     * @author 蔡佳新
     */
    private void putService(Object obj) {
        final Class<?> clazz = obj.getClass();
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            final Annotation[] annotations = interfaceClass.getAnnotations();
            if(Arrays.stream(annotations).anyMatch(this::isRegistService)){
                registServiceContext.put(interfaceClass.getName(), obj);
            }
        }
        putServiceContext(obj, clazz.getSimpleName());
    }

    /**
     * 判断注解是不是 {@link RegistService}的实例
     * @param  annotation 注解
     * @return 如果注解是 {@link RegistService}的实例则返回 true
     * @author 蔡佳新
     */
    private boolean isRegistService(Annotation annotation) {
        return annotation instanceof RegistService;
    }

    /**
     * 往注册服务上下文添加服务实现类
     * @param  obj          服务实现类
     * @param  serviceName  服务名
     * @author 蔡佳新
     */
    private void putServiceContext(Object obj, String serviceName) {
        final List<Object> beanList = serviceContext.get(serviceName);
        if(CollectionUtils.isEmpty(beanList)){
            serviceContext.put(serviceName, Lists.newArrayList(obj));
            return;
        }
        beanList.add(obj);
    }

    /**
     * 判断该对象是否跳过注册成服务
     * @param  obj 对象
     * @return 对象没有 {@link RegistService}或 {@link Service}的注解则返回true
     * @author 蔡佳新
     */
    private boolean withoutPut(Object obj) {
        if(obj == null){
            return false;
        }
        final AnnotatedType[] annotatedInterfaces = obj.getClass().getAnnotatedInterfaces();
        if(ArrayUtils.isEmpty(annotatedInterfaces)){
            return true;
        }
        for (AnnotatedType annotatedInterface : annotatedInterfaces) {
            if(annotatedInterface instanceof Service){
                return true;
            }
        }
        return false;
    }
    //***********************************************injectServiceContext***********************************************
    /**
     * 对订阅服务上下文的服务进行注入
     * @author 蔡佳新
     */
    private void injectServiceContext(){
        final List<Object> serviceList = serviceContext.get(ServiceContextSub.class.getName());
        if(CollectionUtils.isEmpty(serviceList)){
            return;
        }
        for (Object obj : serviceList) {
            final ServiceContextSub serviceContextSub = (ServiceContextSub)obj;
            serviceContextSub.setServiceContext(serviceContext);
        }
    }
    //***********************************************putMethodContext***********************************************

    /**
     * 往methodContext中添加 所有服务实例的所有方法
     * @author 蔡佳新
     */
    private void putAllMethod(){
        registServiceContext.keySet().forEach(this::putMethodContext);
    }

    /**
     * 往methodContext中添加 服务实例的所有方法
     * @param  interfaceName 接口名
     * @author 蔡佳新
     */
    private void putMethodContext(String interfaceName){
        final Class<?> interfaceClazz;
        try {
            interfaceClazz = Class.forName(interfaceName);
        } catch (ClassNotFoundException e) {
            throw new ScanExc(e);
        }
        final Method[] methods = interfaceClazz.getMethods();
        Arrays.stream(methods).forEach(method ->{
            final MethodMark methodMark = createMethodMark(method.getName(), method.getParameterTypes());
            final Map<MethodMark, Method> methodMarkMethodMap = registMethodContext.get(interfaceName);
            if(MapUtils.isEmpty(methodMarkMethodMap)){
                final Map<MethodMark, Method> methodMap = Maps.newHashMap();
                methodMap.put(methodMark, method);
                registMethodContext.put(interfaceName, methodMap);
            }else {
                methodMarkMethodMap.put(methodMark, method);
            }
        });
    }

    /**
     * 生成 {@link MethodMark} 实例
     * @param  methodName    方法名
     * @param  paramClassArr 参数class对象数组
     * @return {@link MethodMark}
     * @author 蔡佳新
     */
    private MethodMark createMethodMark(String methodName, Class<?>[] paramClassArr) {
        if(ArrayUtils.isEmpty(paramClassArr)){
            return MethodMark.builder()
                             .methodName(methodName)
                             .build();
        }
        return MethodMark.builder()
                         .methodName(methodName)
                         .argMarkArrStr(MethodMark.joinArgMarkArrToString(paramClassArr))
                         .build();
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
