package com.github.howieyoung91.farseer.pojo;

import com.github.howieyoung91.farseer.entity.Index;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexInfo {
    private Integer count;
    private Double  score;

    public static IndexInfo from(Index index) {
        IndexInfo indexInfo = new IndexInfo();
        indexInfo.count = index.getCount();
        indexInfo.score = index.getScore();
        return indexInfo;
    }
}
