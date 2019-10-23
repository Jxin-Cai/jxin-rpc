package com.jxin.rpc.core.mark;

import lombok.Builder;
import lombok.Data;

/**
 * 服务标示
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 19:32
 */
@Data
@Builder
public class ServerMark {
    /**服务名*/
    private final String application;
    /**接口全路径名*/
    private final String interfaceName;
    /**方法名*/
    private final String methodName;
    /**参数标识符名 ,号拼接*/
    private final String argMarkArrStr;

/*    public int length(){
       return Integer.BYTES + getApplication().getBytes(StandardCharsets.UTF_8).length
              + Integer.BYTES + getInterfaceName().getBytes(StandardCharsets.UTF_8).length
              + Integer.BYTES + getMethodName().getBytes(StandardCharsets.UTF_8).length
              + Integer.BYTES + getArgMarkArrStr().getBytes(StandardCharsets.UTF_8).length;
    }*/
}
