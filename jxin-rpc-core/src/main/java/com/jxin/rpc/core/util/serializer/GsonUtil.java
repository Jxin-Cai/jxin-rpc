package com.jxin.rpc.core.util.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * gson 解析工具
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/10/23 20:04
 */
public class GsonUtil {
    /**Gson实例*/
    private static final Gson GSON = new Gson();

    /**
     * 将object对象转成json字符串
     * @param  object 对象
     * @return json字符串
     * @author 蔡佳新
     */
    public static String GsonToStr(Object object) {
        return GSON.toJson(object);
    }

    /**
     * 将json转成泛型bean
     * @param  json  json字符串
     * @param  clazz 类的字节码对象
     * @return bean对象
     * @author 蔡佳新
     */
    public static <T> T GsonToBean(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * 转成list
     * 解决泛型问题
     * @param  json  json字符串
     * @param  clazz 类的字节码对象
     * @param  <T>   对象的泛型
     * @return bean对象列表
     * @author 蔡佳新
     */
    public static <T> List<T> GsonToList(String json, Class<T> clazz) {
        final JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        final List<T> result = new ArrayList<>(array.size());
        for(final JsonElement elem : array){
            result.add(GSON.fromJson(elem, clazz));
        }
        return result;
    }

    /**
     * 转成map的
     * @param  json json字符串
     * @return map
     * @author 蔡佳新
     */
    public static <T> Map<String, T> GsonToMaps(String json) {
        return GSON.fromJson(json, new TypeToken<Map<String, T>>() {}.getType());
    }

    /**
     * 转成list中有map的
     * @param  json json字符串
     * @return list中有map的list
     * @author 蔡佳新
     */
    public static <T> List<Map<String, T>> GsonToListMaps(String json) {
        return GSON.fromJson(json, new TypeToken<List<Map<String, T>>>() {}.getType());
    }
}
