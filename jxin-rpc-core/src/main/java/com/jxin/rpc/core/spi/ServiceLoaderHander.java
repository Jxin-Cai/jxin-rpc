package com.jxin.rpc.core.spi;

import com.jxin.rpc.core.exc.ServiceLoaderExc;
import com.jxin.rpc.core.inject.Singleton;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * SPI类加载器执行器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 20:25
 */
public class ServiceLoaderHander {
    /**单例服务的容器*/
    private static final Map<String/*服务名*/, Object/*服务实现类*/> SINGLETON_SERVICE_MAP = new HashMap<>();

    /**
     * 单服务加载器
     * @param  service 服务实例
     * @param  <T>     实例的泛型
     * @return 服务实例
     * @author 蔡佳新
     */
    public static synchronized <T> T load(Class<T> service) {
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                            .map(ServiceLoaderHander::singletonFilter)
                            .findFirst().orElseThrow(ServiceLoaderExc::new);
    }
    /**
     * 批量服务加载器
     * @param  service 服务实例
     * @param  <T>     实例的泛型
     * @return 服务实例的集合
     * @author 蔡佳新
     */
    public static synchronized <T> Collection<T> loadAll(Class<T> service) {
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                            .map(ServiceLoaderHander::singletonFilter).collect(Collectors.toList());
    }
    /**
     * 单例服务过滤器
     * 1.如果服务带有单例注解{@link Singleton}, 则仅取最先注册的实例
     * 2.如果服务没有单例注解,则可以注册多实例
     * @param  service 服务实例
     * @param  <T>     实例的泛型
     * @return 服务实例
     * @author 蔡佳新
     */
    @SuppressWarnings("unchecked")
    private static <T> T singletonFilter(T service) {
        if(service.getClass().isAnnotationPresent(Singleton.class)) {
            final Object singletonInstance = SINGLETON_SERVICE_MAP.putIfAbsent(service.getClass().getName(), service);
            return singletonInstance == null ? service : (T) singletonInstance;
        }
        return service;
    }
}
