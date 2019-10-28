package com.jxin.rpc.client.exc;

/**
 * 生成feign异常类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 16:59
 */
public class InitFeignExc extends RuntimeException{
    public InitFeignExc(String messageTemplate, Object... params){
        super(String.format(messageTemplate, params));
    }
    public InitFeignExc(String message){
        super(message);
    }
    public InitFeignExc(Throwable e){
        super(e);
    }
    public InitFeignExc(){
        super();
    }
}
