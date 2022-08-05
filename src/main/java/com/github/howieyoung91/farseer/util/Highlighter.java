package com.github.howieyoung91.farseer.util;

import java.util.HashMap;
import java.util.Map;

public class Highlighter {
    private final String text;
    private       String prefix;
    private       String suffix;

    public Highlighter(String text, String prefix, String suffix) {
        this.text = text;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String highlight(String keyword) {
        if (prefix == null && suffix == null) {
            return text;
        }
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        return highlight(text, keyword, prefix, suffix);
    }

    // todo 多模匹配
    public static String highlight(String text, String keyword, String prefix, String suffix) {
        Map<Integer, String> map        = new HashMap<>();
        int                  startIndex = 0;     // 关键字起始索引
        int                  endIndex   = 0;     // 关键字结尾索引
        boolean              isMatching = false; // 进入关键字匹配标志
        // 遍历原始字符串
        for (int i = 0; i < text.length(); i++) {
            // 遍历关键词字符串
            for (char keyChar : keyword.toCharArray()) {
                if (Character.toLowerCase(text.charAt(i)) == Character.toLowerCase(keyChar)) {
                    // 匹配到关键字第一个字符相等后
                    if (!isMatching) {
                        startIndex = i;      // 将起始索引赋值为当前遍历原始字符串索引
                        endIndex = i;        // 将结尾索引也赋值为相同到当前索引
                        isMatching = true;   // 标记进入匹配模式
                    }
                    endIndex++;              // 将结尾索引自增
                    i = endIndex;            // 将遍历原始字符串到索引定位到结尾索引，避免重复遍历
                }
                else {
                    isMatching = false;      // 如果不相等则结束匹配模式
                }
            }

            // 如果结束索引与起始索引相减到值为关键字到长度则表明匹配到完整到关键字
            if (endIndex - startIndex == keyword.length()) {
                // 将起始索引和结束索引对应到高亮标签put到哈希表中，并且重置匹配标识
                map.put(startIndex, prefix);
                map.put(endIndex, suffix);
                isMatching = false;
            }
        }

        StringBuilder builder = new StringBuilder();
        // 遍历原始字符串，通过哈希表中存储到高亮索引，将标签拼接到原始字符串里面
        for (int i = 0; i < text.length(); i++) {
            builder.append(map.getOrDefault(i, "")).append(text.charAt(i));
        }
        // 下面这一行是用来处理特殊情况，即关键字在最后到情况
        builder.append(map.getOrDefault(text.length(), ""));
        return builder.toString();
    }

}
