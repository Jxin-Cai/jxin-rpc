package com.jxin.rpc.client.call.msg.manage;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 请求管理器
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 15:38
 */
public class ReqManager implements Closeable {
    /**定时任务的调用间隔*/
    private static final long LOOP_TIME = 10L;
    /**线程安全的 全局响应实体容器*/
    private final Map<Integer/*requestId*/, RspFuture> futureMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;

    /**
     * 在构造器中开启清理超时RspFuture的定时job
     * @author 蔡佳新
     */
    public ReqManager() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures, 0, LOOP_TIME, TimeUnit.SECONDS);
    }

    /**
     * 往全局响应实体容器中添加新的响应实体
     * @param  rspFuture 聚合Future类型响应消息实体的响应体
     * @author 蔡佳新
     */
    public void put(RspFuture rspFuture) {
        futureMap.put(rspFuture.getRequestId(), rspFuture);
    }

    /**
     * 删除全局响应实体容器中指定 请求id的参数
     * @param  requestId 请求id
     * @return 被剔除的 value,如果请求id不存在则返回null
     * @author 蔡佳新
     */
    public RspFuture remove(int requestId) {
        return futureMap.remove(requestId);
    }
    /**
     * 剔除已经超时的响应实体(防止"内存泄漏")
     * @author 蔡佳新
     */
    private void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> System.nanoTime() - entry.getValue().getTimestamp() > LOOP_TIME * 1000000000L);
    }

    @Override
    public void close(){
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
