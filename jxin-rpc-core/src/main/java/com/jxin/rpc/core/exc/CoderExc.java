package com.jxin.rpc.core.exc;

/**
 * 编译异常类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 21:24
 */
public class CoderExc extends RuntimeException{
    public CoderExc(String messageTemplate, Object... params){
        super(String.format(messageTemplate, params));
    }
    public CoderExc(){
        super();
    }
}
