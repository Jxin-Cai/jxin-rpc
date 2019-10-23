package com.jxin.rpc.core.exc;

/**
 * 序列化异常类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/21 21:08
 */
public class SerializeExc extends RuntimeException{
    public SerializeExc(String messageTemplate, Object... params){
        super(String.format(messageTemplate, params));
    }
    public SerializeExc(Throwable throwable, String messageTemplate, Object... params) {
        super(String.format(messageTemplate, params), throwable);
    }
    public SerializeExc(){
        super();
    }
}
