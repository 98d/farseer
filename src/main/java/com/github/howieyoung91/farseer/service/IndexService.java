package com.github.howieyoung91.farseer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.entity.Index;
import com.github.howieyoung91.farseer.entity.Token;
import com.github.howieyoung91.farseer.mapper.IndexMapper;
import com.github.howieyoung91.farseer.util.Factory;
import com.huaban.analysis.jieba.JiebaSegmenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class IndexService {
    @Resource
    private IndexMapper     indexMapper;
    @Resource
    private DocumentService documentService;
    @Resource
    private JiebaSegmenter  segmenter;
    @Resource
    private TokenService    tokenService;

    // ========================================   public methods   =========================================
    @Transactional
    public Collection<Index> index(List<Document> documents) {
        documentService.insert(documents);
        Collection<Index> indices = buildIndices(documents);
        indexMapper.insertBatch(indices);
        return indices;
    }

    public List<Document> search(String segment, Page<Index> page) {
        Token token = tokenService.selectToken(segment);
        if (token == null) {
            return new ArrayList<>(0);
        }
        List<Index> indices = selectIndices(token, page);
        return documentService.selectIndexedDocuments(indices);
    }

    // ========================================   public methods   =========================================

    private Collection<Index> buildIndices(Collection<Document> documents) {
        ArrayList<Index> indices = new ArrayList<>();
        for (Document document : documents) {
            indices.addAll(buildIndices(document));
        }
        return indices;
    }

    /**
     * 生成 document 的倒排索引
     *
     * @param document 一篇文档
     * @return 生成的索引
     */
    private Collection<Index> buildIndices(Document document) {
        List<Index>        indices  = new ArrayList<>();
        Collection<String> segments = tokenize(document.getText());
        for (String segment : segments) {
            Token token = selectToken(segment);
            Index index = Index.of(token.getId(), document.getId());
            indices.add(index);
        }
        return indices;
    }

    private List<Index> selectIndices(Token token, Page<Index> page) {
        return indexMapper.selectPage(
                        Factory.createPage(page.getCurrent(), page.getSize()),
                        Factory.createLambdaQueryWrapper(Index.class).eq(Index::getTokenId, token.getId()))
                .getRecords();
    }

    /**
     * 对一段文本进行分词
     */
    private Collection<String> tokenize(String text) {
        return new HashSet<>(segmenter.sentenceProcess(text));
    }

    private Token selectToken(String segment) {
        Token token = tokenService.selectToken(segment);
        if (token == null) {
            token = tokenService.insert(segment);
        }
        return token;
    }
}