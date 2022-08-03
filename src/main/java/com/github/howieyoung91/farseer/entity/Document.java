package com.github.howieyoung91.farseer.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Document {
    @TableId(type = IdType.ASSIGN_ID)
    protected String id;
    protected String text;
    protected String content; // 必须是 json

    public static Document fromJSON(String keyword, String content) {
        Document document = new Document();
        document.setText(keyword);
        document.setContent(content);
        return document;
    }
}
