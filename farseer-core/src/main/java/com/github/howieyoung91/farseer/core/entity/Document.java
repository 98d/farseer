package com.github.howieyoung91.farseer.core.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

public class Document implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String text;
    private String content; // 必须是 json
    private String highlightPrefix;
    private String highlightSuffix;

    public static Document fromJSON(String keyword, String content) {
        Document document = new Document();
        document.setText(keyword);
        document.setContent(content);
        return document;
    }

    public Document setHighlight(String prefix, String suffix) {
        highlightPrefix = prefix;
        highlightSuffix = suffix;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHighlightPrefix() {
        return highlightPrefix;
    }

    public void setHighlightPrefix(String highlightPrefix) {
        this.highlightPrefix = highlightPrefix;
    }

    public String getHighlightSuffix() {
        return highlightSuffix;
    }

    public void setHighlightSuffix(String highlightSuffix) {
        this.highlightSuffix = highlightSuffix;
    }
}
