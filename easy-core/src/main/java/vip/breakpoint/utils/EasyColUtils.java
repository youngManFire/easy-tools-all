package vip.breakpoint.utils;

import java.util.Collection;
import java.util.Map;

/**
 * 集合的操作
 *
 * @author : breakpoint
 * create on 2022/09/05
 * 欢迎关注公众号 《代码废柴》
 */
public class EasyColUtils {

    /**
     * 判断是为空
     *
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return null != collection && !collection.isEmpty();
    }

    /**
     * 判断是否是空的
     *
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return !isNotEmpty(collection);
    }

    /**
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return null != map && !map.isEmpty();
    }

    /**
     * 判断是否是空的
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return !isNotEmpty(map);
    }
}
