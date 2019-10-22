package com.jxin.rpc.core.call.feign;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

/**
 * feign 客户端
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 17:52
 */
public interface FeignClient extends Closeable {
    Feign createFeign(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException;
}
