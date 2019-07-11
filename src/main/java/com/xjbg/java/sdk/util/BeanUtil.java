package com.xjbg.java.sdk.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author kesc
 * @since 2018/1/17
 */
@Slf4j
public final class BeanUtil extends BeanUtils {

    public static <S, R> List<R> convert(List<S> source, Class<R> clazz) {
        if (CollectionUtil.isEmpty(source)) {
            return Collections.emptyList();
        }
        List<R> result = Lists.newArrayList();
        source.forEach(x -> {
            R instance = instantiateClass(clazz);
            BeanUtils.copyProperties(x, instance);
            result.add(instance);
        });
        return result;
    }

    public static <S, R> R convert(S source, Class<R> clazz) {
        R instance = instantiateClass(clazz);
        if (source == null) {
            return instance;
        }
        BeanUtils.copyProperties(source, instance);
        return instance;
    }
}
