package com.github.howieyoung91.farseer.data.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.github.howieyoung91.farseer.core.pojo.DocumentVo;
import com.github.howieyoung91.farseer.data.AbstractDocumentSource;
import com.github.howieyoung91.farseer.data.convert.DocumentVoConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/13 16:07]
 */
@Component
// @ConditionalOnBean(Canal.class)
@Slf4j
public class CanalDocumentSource extends AbstractDocumentSource<CanalEntry.RowChange> {
    @Autowired(required = false)
    private Canal canal;

    @Autowired(required = false)
    public CanalDocumentSource(DocumentVoConverter<CanalEntry.RowChange> converter) {
        super(converter);
    }

    @PostConstruct
    public void init() {
        canal.connect().subscribe();
    }

    @Override
    public List<DocumentVo> getDocuments() {
        ArrayList<DocumentVo>      result     = new ArrayList<>();
        List<CanalEntry.RowChange> rowChanges = canal.get();
        log.info("{}", rowChanges);
        if (rowChanges.isEmpty()) {
            return result;
        }
        DocumentVoConverter<CanalEntry.RowChange> converter = getDocumentConverter();
        for (CanalEntry.RowChange rowChange : rowChanges) {
            List<DocumentVo> d = converter.convert(rowChange);
            result.addAll(d);
        }
        return result;
    }
}
