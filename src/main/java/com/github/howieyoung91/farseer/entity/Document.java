package com.github.howieyoung91.farseer.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Document {
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

    public Document highlight(String prefix, String suffix) {
        highlightPrefix = prefix;
        highlightSuffix = suffix;
        return this;
    }
}
