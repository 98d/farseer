package com.github.howieyoung91.farseer.pojo;

import lombok.Data;

@Data
public class DocumentVo {
    private String text;
    private String content;
    private String highlightPrefix;
    private String highlightSuffix;
}
