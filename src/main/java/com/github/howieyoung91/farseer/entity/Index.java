package com.github.howieyoung91.farseer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("`index`")
@AllArgsConstructor
@NoArgsConstructor
public class Index {
    @TableId(type = IdType.ASSIGN_ID)
    private String  id;
    private String  tokenId;
    private String  documentId;
    private Integer count;
    private Double  score;

    public static Index fromToken(String tokenId) {
        Index record = new Index();
        record.setTokenId(tokenId);
        return record;
    }

    public static Index of(String tokenId, String documentId) {
        Index index = Index.fromToken(tokenId);
        index.setDocumentId(documentId);
        return index;
    }
}
