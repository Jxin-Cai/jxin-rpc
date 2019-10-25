package com.jxin.rpc.core.scan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jxin.rpc.core.inject.RegistService;
import com.jxin.rpc.core.inject.Service;
import com.jxin.rpc.core.call.msg.mark.MethodMark;
import com.jxin.rpc.core.util.serializer.ArgMarkUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 应用服务上下文
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/24 15:59
 */
@Slf4j
public class ApplicationContext {
    /**全部服务上下文*/
    private Map<String/*serviceName*/, List<Object>/*serviceImplList*/> serviceContext = Maps.newHashMap();
    /**注册服务上下文*/
    private Map<String/*interfaceName*/, List<Object>/*serviceImplList*/> registServiceContext = Maps.newHashMap();
    /**注册服务方法上下文*/
    private Map<Object/*serviceImpl*/, Map<MethodMark, Method>/*methodMap*/> registMethodContext = Maps.newHashMap();


    public ApplicationContext(String pkg) {
        // 扫包
        scanPackage(pkg);
        // 注入服务上下文
        injectServiceContext();
        // 往methodContext中添加 所有服务实例的所有方法
        putAllMethod();
    }

    /**
     * 根据接口类全路径名 获取服务名
     * @param  interfaceName 接口类全路径名
     * @return 实现类列表
     * @author 蔡佳新
     */
    public List<Object> getRegistServiceList(String interfaceName) {
        return registServiceContext.get(interfaceName);
    }
    /**
     * 根据实现类名 获取方法Map
     * @param  obj 实现类名
     * @return 实现类列表
     * @author 蔡佳新
     */
    public Map<MethodMark, Method> getRegistMethod(Object obj) {
        return registMethodContext.get(obj);
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
        final File pkgDir = new File(url.getFile());
        final File fs[] = pkgDir.listFiles(file -> {
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
    public void putService(Object obj) {
        final Class<?> clazz = obj.getClass();
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            final AnnotatedType[] annotatedInterfaces = interfaceClass.getAnnotatedInterfaces();
            if(Arrays.stream(annotatedInterfaces).anyMatch(this::isRegistService)){
                putServiceContext(obj, interfaceClass.getName(), registServiceContext);
            }
        }
        putServiceContext(obj, clazz.getCanonicalName(), serviceContext);
    }

    /**
     * 判断注解是不是 {@link RegistService}的实例
     * @param  annotated 注解
     * @return 如果注解是 {@link RegistService}的实例则返回 true
     * @author 蔡佳新
     */
    private boolean isRegistService(AnnotatedType annotated) {
        return annotated instanceof RegistService;
    }

    /**
     * 往注册服务上下文添加服务实现类
     * @param  obj          服务实现类
     * @param  serviceName  服务名
     * @param  context 上下文
     * @author 蔡佳新
     */
    private void putServiceContext(Object obj, String serviceName, Map<String, List<Object>> context) {
        final List<Object> beanList = context.get(serviceName);
        if(CollectionUtils.isEmpty(beanList)){
            context.put(serviceName, Lists.newArrayList(obj));
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
        registServiceContext.forEach((k, v) -> v.forEach(this::putMethodContext));
    }

    /**
     * 往methodContext中添加 服务实例的所有方法
     * @param  obj 服务实例
     * @author 蔡佳新
     */
    private void putMethodContext(Object obj){
        final Method[] methods = obj.getClass().getMethods();
        Arrays.stream(methods).forEach(method ->{
            final MethodMark methodMark = createMethodMark(method.getName(), method.getParameterTypes());
            final Map<MethodMark, Method> methodMarkMethodMap = registMethodContext.get(obj);
            if(MapUtils.isEmpty(methodMarkMethodMap)){
                final Map<MethodMark, Method> methodMap = Maps.newHashMap();
                methodMap.put(methodMark, method);
                registMethodContext.put(obj, methodMap);
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
        final StringBuilder argMarkArrStr = new StringBuilder(ArgMarkUtil.getMark(paramClassArr[0]));
        Arrays.stream(paramClassArr, 1, paramClassArr.length -1)
              .forEach(clazz -> argMarkArrStr.append(",").append(ArgMarkUtil.getMark(clazz)));
        return MethodMark.builder()
                         .methodName(methodName)
                         .argMarkArrStr(argMarkArrStr.toString())
                         .build();
    }
}
