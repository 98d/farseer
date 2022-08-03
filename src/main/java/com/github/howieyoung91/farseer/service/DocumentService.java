package com.github.howieyoung91.farseer.service;

import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.entity.Index;
import com.github.howieyoung91.farseer.mapper.DocumentMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {
    @Resource
    private DocumentMapper documentMapper;

    public void insert(Document document) {
        document.setId(null);
        documentMapper.insert(document);
    }

    public int insert(List<Document> documents) {
        return documentMapper.insertBatch(documents);
    }

    /**
     * 根据一组倒排索引查询对应对文档
     *
     * @param indices 一组倒排索引
     * @return 文档
     */
    public List<Document> selectIndexedDocuments(List<Index> indices) {
        List<Document> documents = new ArrayList<>();
        for (Index index : indices) {
            String   documentId = index.getDocumentId();
            Document document   = documentMapper.selectById(documentId);
            documents.add(document);
        }
        return documents;
    }

}
