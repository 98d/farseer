package com.github.howieyoung91.farseer.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Objects;

public class Factory {
    public static        Object NULL          = new Object();
    private static final long   PAGE_MAX_SIZE = 1000;


    public static <T> LambdaQueryWrapper<T> createLambdaQueryWrapper(Class<T> clazz) {
        return new LambdaQueryWrapper<T>();
    }

    public static <T> LambdaQueryWrapper<T> createLambdaQueryWrapper(T query) {
        return new LambdaQueryWrapper<>(query);
    }

    public static <T> Page<T> resolvePage(Page<T> page) {
        Objects.requireNonNull(page, "page cannot be null!");
        long current = page.getCurrent();
        long size    = page.getSize();
        if (current < 0) {
            page.setCurrent(0);
        }
        if (size < 0) {
            page.setSize(0);
        }
        else if (size > PAGE_MAX_SIZE) {
            page.setSize(PAGE_MAX_SIZE);
        }
        return page;
    }

    public static <T> Page<T> createPage(long current, long size, long maxSize) {
        if (current < 0) {
            current = 0;
        }
        if (size < 0) {
            size = 0;
        }
        else if (size > maxSize) {
            size = maxSize;
        }
        return new Page<>(current, size);
    }

    public static <T> Page<T> createPage(long current, long size) {
        return createPage(current, size, PAGE_MAX_SIZE);
    }
}
