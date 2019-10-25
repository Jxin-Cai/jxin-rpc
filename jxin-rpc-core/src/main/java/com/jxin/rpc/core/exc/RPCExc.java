package com.jxin.rpc.core.exc;

/**
 * rpc调用异常类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 16:59
 */
public class RPCExc extends RuntimeException{
    public RPCExc(String messageTemplate, Object... params){
        super(String.format(messageTemplate, params));
    }
    public RPCExc(String message){
        super(message);
    }
    public RPCExc(Throwable e){
        super(e);
    }
    public RPCExc(){
        super();
    }
}
