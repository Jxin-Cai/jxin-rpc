package com.jxin.rpc.center.exc;

/**
 * 注册中心异常类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/28 21:26
 */
public class RegisterCenterExc extends RuntimeException{
    public RegisterCenterExc(String messageTemplate, Object... params){
        super(String.format(messageTemplate, params));
    }
    public RegisterCenterExc(String message){
        super(message);
    }
    public RegisterCenterExc(Throwable e){
        super(e);
    }
    public RegisterCenterExc(){
        super();
    }
}
