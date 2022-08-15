package com.github.howieyoung91.farseer.data.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.github.howieyoung91.farseer.core.pojo.DocumentVo;
import com.github.howieyoung91.farseer.data.canal.Canal;
import com.github.howieyoung91.farseer.data.convert.DocumentVoConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/15 12:20]
 */
@Configuration
public class DataSourceConfig {
    @Bean
    Canal canal() {
        Canal canal = new Canal();
        canal.setFilter("farseer\\.row_document");
        return canal;
    }

    @Bean
    DocumentVoConverter<CanalEntry.RowChange> converter() {
        return rowChange -> {
            List<DocumentVo>         result    = new ArrayList<>();
            CanalEntry.EventType     eventType = rowChange.getEventType();
            List<CanalEntry.RowData> rowData   = rowChange.getRowDatasList();
            if (eventType == CanalEntry.EventType.INSERT) {
                for (CanalEntry.RowData rowDatum : rowData) {
                    DocumentVo documentVo = populateDocument(rowDatum);
                    result.add(documentVo);
                }
            }
            else if (eventType == CanalEntry.EventType.UPDATE) {

            }
            else if (eventType == CanalEntry.EventType.DELETE) {

            }
            return result;
        };
    }

    private static DocumentVo populateDocument(CanalEntry.RowData rowDatum) {
        // String     id         = rowDatum.getAfterColumns(0).getValue();
        String     text       = rowDatum.getAfterColumns(1).getValue();
        String     content    = rowDatum.getAfterColumns(2).getValue();
        DocumentVo documentVo = new DocumentVo();
        documentVo.setText(text);
        documentVo.setContent(content);
        documentVo.setHighlightPrefix("<font style='color:red'>");
        documentVo.setHighlightSuffix("</font>");
        return documentVo;
    }
}
