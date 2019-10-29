package com.jxin.rpc.core.call.msg.manage;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 抽象请求管理器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:38
 */
public abstract class AbstractReqManager implements ReqManager{
    /**定时任务的调用间隔*/
    protected static final long LOOP_TIME = 10L;
    /**线程安全的 全局响应实体容器*/
    protected final Map<String/*requestId*/, RspFuture> futureMap = new ConcurrentHashMap<>();
    /**定时任务执行线程池*/
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    /**定时任务的 Future对象*/
    private final ScheduledFuture scheduledFuture;

    /**
     * 在构造器中开启清理超时RspFuture的定时job
     * @author 蔡佳新
     */
    public AbstractReqManager() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures,
                                                                       0,
                                                                       LOOP_TIME,
                                                                       TimeUnit.SECONDS);
    }

    /**
     * 往全局响应实体容器中添加新的响应实体
     * @param  rspFuture 聚合Future类型响应消息实体的响应体
     * @author 蔡佳新
     */
    @Override
    public void put(RspFuture rspFuture) throws InterruptedException, TimeoutException {
        futureMap.put(rspFuture.getRequestId(), rspFuture);
    }


    /**
     * 删除全局响应实体容器中指定 请求id的参数
     * @param  requestId 请求id
     * @return 被剔除的 value,如果请求id不存在则返回null
     * @author 蔡佳新
     */
    @Override
    public RspFuture remove(String requestId) {
        return futureMap.remove(requestId);
    }

    /**
     * 剔除已经超时的响应实体(防止"内存泄漏")
     * @author 蔡佳新
     */
    protected abstract void removeTimeoutFutures();


    @Override
    public void close(){
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
