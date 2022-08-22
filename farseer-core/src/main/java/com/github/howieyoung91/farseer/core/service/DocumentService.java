package com.github.howieyoung91.farseer.core.service;

import com.github.howieyoung91.farseer.core.config.CacheKeys;
import com.github.howieyoung91.farseer.core.entity.Document;
import com.github.howieyoung91.farseer.core.entity.Index;
import com.github.howieyoung91.farseer.core.mapper.DocumentMapper;
import com.github.howieyoung91.farseer.core.util.Redis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DocumentService {
    @Resource
    private DocumentMapper documentMapper;
    @Resource
    private Redis          redis;

    // ========================================   public methods   =========================================
    @Transactional
    public void insert(Document document) {
        document.setId(null);
        documentMapper.insert(document);
        cache(document);
    }

    @Transactional
    public int insert(List<Document> documents) {
        Integer count = documentMapper.insertBatch(documents);
        for (Document document : documents) {
            cache(document);
        }
        return count;
    }

    @Transactional
    public void deleteById(String documentId) {
        redis.cfadd(CacheKeys.documentIdCuckooFilter(), documentId);
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

    // ========================================   public methods   =========================================

    private Document selectDocumentById(String documentId) {
        if (redis.cfexists(CacheKeys.documentIdCuckooFilter(), documentId)) {
            return null;
        }
        Document document = (Document) redis.get(CacheKeys.documentKey(documentId));
        if (document == null) {
            document = doSelectDocumentByIdFromDatabase(documentId);
        }
        return document;
    }

    private Document doSelectDocumentByIdFromDatabase(String documentId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            cacheIntoCuckoo(documentId);
        }
        else {
            cache(document);
        }
        return document;
    }

    private void cacheIntoCuckoo(String documentId) {
        String filterKey = CacheKeys.documentIdCuckooFilter();
        log.info("cuckoo filter [{}] add key [{}]", filterKey, documentId);
        redis.cfadd(filterKey, documentId);
    }

    private void cache(Document document) {
        redis.kvSet(CacheKeys.documentKey(document.getId()), document);
    }
}
