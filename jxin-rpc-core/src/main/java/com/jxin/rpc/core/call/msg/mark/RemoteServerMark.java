package com.jxin.rpc.core.call.msg.mark;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 远程服务标示
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/31 20:43
 */
@Data
@Builder
public class RemoteServerMark {
    /**远程服务列表*/
    private List<ServerMark> remoteServerList;
}
