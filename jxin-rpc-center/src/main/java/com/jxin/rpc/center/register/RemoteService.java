package com.jxin.rpc.center.register;

import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.util.List;

/**
 * 订阅的远程服务实例
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/29 20:08
 */
@Data
@Builder
public class RemoteService {
    /**服务名*/
    private String applicationName;
    /**服务列表*/
    private List<String/*interface*/> serviceList;
    /**服务uril列表*/
    private List<URI> serviceUriList;
}
