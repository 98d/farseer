package com.github.howieyoung91.farseer.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Objects;

public class Factory {
    public static        Object NULL              = new Object();
    private static final long   PAGE_MAX_SIZE     = 1000;
    private static final long   PAGE_DEFAULT_SIZE = 20;

    public static <T> LambdaQueryWrapper<T> createLambdaQueryWrapper(Class<T> clazz) {
        return new LambdaQueryWrapper<T>();
    }

    public static <T> LambdaQueryWrapper<T> createLambdaQueryWrapper(T query) {
        return new LambdaQueryWrapper<>(query);
    }

    public static <T> Page<T> resolvePage(Page<T> page, long maxSize) {
        Objects.requireNonNull(page, "page cannot be null!");
        long current = page.getCurrent();
        long size    = page.getSize();
        if (current < 0) {
            page.setCurrent(0);
        }
        if (size < 0) {
            page.setSize(0);
        }
        else if (size > maxSize) {
            page.setSize(maxSize);
        }
        return page;
    }

    public static <T> Page<T> resolvePage(Page<T> page) {
        return resolvePage(page, PAGE_MAX_SIZE);
    }

    public static <T> Page<T> createPage(long current, long size, long maxSize) {
        Page<T> page = new Page<>(current, size);
        return resolvePage(page, maxSize);
    }

    public static <T> Page<T> createPage(long current, long size) {
        Page<T> page = new Page<>(current, size);
        return resolvePage(page);
    }
}
