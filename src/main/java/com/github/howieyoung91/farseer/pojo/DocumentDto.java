package com.github.howieyoung91.farseer.pojo;

import com.github.howieyoung91.farseer.entity.Document;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class DocumentDto {
    private String  id;
    private String  text;
    private String  content;
    private Details details = new Details();

    public static DocumentDto from(Document other) {
        DocumentDto dto = new DocumentDto();
        dto.setId(other.getId());
        dto.setText(other.getText());
        dto.setContent(other.getContent());
        return dto;
    }

    public void addIndexInfo(String token, IndexInfo indexInfo) {
        details.hits.put(token, indexInfo);
    }

    @Data
    static class Details {
        Map<String, IndexInfo> hits = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocumentDto that = (DocumentDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, content, details);
    }
}
