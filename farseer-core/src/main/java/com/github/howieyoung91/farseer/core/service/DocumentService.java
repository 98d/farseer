package com.github.howieyoung91.farseer.core.service;

import com.github.howieyoung91.farseer.core.config.CacheKeys;
import com.github.howieyoung91.farseer.core.entity.Document;
import com.github.howieyoung91.farseer.core.entity.Index;
import com.github.howieyoung91.farseer.core.mapper.DocumentMapper;
import com.github.howieyoung91.farseer.core.util.Redis;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {
    @Resource
    private DocumentMapper documentMapper;
    @Resource
    private Redis          redis;

    public void insert(Document document) {
        document.setId(null);
        documentMapper.insert(document);
        cache(document);
    }

    public int insert(List<Document> documents) {
        Integer count = documentMapper.insertBatch(documents);
        for (Document document : documents) {
            cache(document);
        }
        return count;
    }

    public void deleteById(String documentId) {
        int count = documentMapper.deleteById(documentId);
        if (count != 0) {
            redis.del(CacheKeys.documentKey(documentId));
        }
    }

    /**
     * 根据一组倒排索引查询对应对文档
     *
     * @param indices 一组倒排索引
     * @return 文档
     */
    public List<Document> selectDocumentsByIndices(List<Index> indices) {
        if (indices.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<Document> documents = new ArrayList<>();
        for (Index index : indices) {
            String   documentId = index.getDocumentId();
            Document document   = selectDocumentById(documentId);
            documents.add(document);
        }
        return documents;
    }

    private Document selectDocumentById(String documentId) {
        Document document = (Document) redis.get(CacheKeys.documentKey(documentId));
        if (document == null) {
            document = documentMapper.selectById(documentId);
        }
        return document;
    }

    private void cache(Document document) {
        redis.kvSet(CacheKeys.documentKey(document.getId()), document);
    }
}
