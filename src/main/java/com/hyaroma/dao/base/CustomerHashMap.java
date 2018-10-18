package com.hyaroma.dao.base;

import com.hyaroma.dao.utils.ValidateUtil;

import java.util.HashMap;

/**
 * 重写getOrDefault 当值为  null 或者 "null" 或者 "" 就返回指定的默认值
 * @param <K> key
 * @param <V> value
 */
public class CustomerHashMap<K, V> extends HashMap<K, V> {
    /**
     *
     * @param key
     * @param defaultValue 默认值
     * @return
     */
    @Override
    public  V getOrDefault(Object key, V defaultValue) {
       try {
           V v=get(key);
           return (!(ValidateUtil.isNull(String.valueOf(v))))
                   ? v
                   : defaultValue;
       }catch (Exception e){
           return defaultValue;
       }
    }
}
