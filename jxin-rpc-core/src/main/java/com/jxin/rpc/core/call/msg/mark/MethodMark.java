package com.jxin.rpc.core.call.msg.mark;

import com.jxin.rpc.core.util.serializer.ArgMarkUtil;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
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

    /**
     * 逗号拼接的参数类型标示字符串
     * @param  paramClassArr 参数蕾西数组
     * @return 逗号拼接参数类型数组的标示
     * @author 蔡佳新
     */
    public static String joinArgMarkArrToString(Class<?>[] paramClassArr) {
        return String.join(",", Arrays.stream(paramClassArr)
                                                .map(ArgMarkUtil::getMark)
                                                .toArray(String[]::new));
    }
}
