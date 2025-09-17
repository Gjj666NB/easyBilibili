package com.easylive.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @projectName: easybbs
 * @author: Li
 * @description: Json工具类
 */

public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    // 将对象转换为json字符串
    public static String convertObj2Json(Object obj) {
        return JSON.toJSONString(obj);
    }

    // 将json字符串转换为对象
    public static <T> T convertJson2Obj(String jsonStr, Class<T> clazz) {
        return JSONObject.parseObject(jsonStr, clazz);
    }

    // 将json字符串数组转换为集合对象
    public static <T> List<T> convertJsonArray2List(String json, Class<T> clazz) {
        return JSONArray.parseArray(json, clazz);
    }
}
