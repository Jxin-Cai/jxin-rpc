package com.jxin.rpc.core.call.msg.mark;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * 方法标识
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/25 11:03
 */
@Data
@Builder
public class MethodMark {
    /**方法名*/
    private final String methodName;
    /**参数标识符名 ,号拼接*/
    private final String argMarkArrStr;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodMark that = (MethodMark) o;
        return Objects.equals(methodName, that.methodName) &&
                Objects.equals(argMarkArrStr, that.argMarkArrStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, argMarkArrStr);
    }
}
