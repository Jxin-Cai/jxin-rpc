package com.jxin.rpc.server.scan;

import java.util.List;
import java.util.Map;

/**
 * 服务上下文 订阅接口
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/24 17:06
 */
public interface ServiceContextSub {

    void setServiceContext(Map<String/*interfaceName*/, List<Object>/*serviceImplList*/> serviceContext);
}
