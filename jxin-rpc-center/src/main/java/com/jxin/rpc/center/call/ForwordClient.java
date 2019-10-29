package com.jxin.rpc.center.call;

import com.jxin.rpc.core.call.Client;
import com.jxin.rpc.core.call.Sender;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

/**
 * 跳转客户端
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 17:52
 */
public interface ForwordClient extends Client {
    /**
     * 生成发送消息的 消息发送器
     * @param  address           套接字地址
     * @param  connectionTimeout 连接超时时间
     * @return 发送消息的 Fegin接口
     * @throws InterruptedException  建立连接被中断
     * @throws TimeoutException      建立连接超时
     * @throws IllegalStateException 参数异常
     * @author 蔡佳新
     */
    Sender createSender(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException;
}
