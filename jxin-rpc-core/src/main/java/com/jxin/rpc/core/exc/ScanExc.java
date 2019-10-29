package com.jxin.rpc.core.exc;

/**
 * 扫包异常类
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/22 16:59
 */
public class ScanExc extends RuntimeException{
    public ScanExc(String messageTemplate, Object... params){
        super(String.format(messageTemplate, params));
    }
    public ScanExc(String message){
        super(message);
    }
    public ScanExc(Throwable e){
        super(e);
    }
    public ScanExc(){
        super();
    }
}
