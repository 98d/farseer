package com.github.howieyoung91.farseer.service.support;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.entity.Index;
import com.github.howieyoung91.farseer.entity.Token;
import com.github.howieyoung91.farseer.mapper.IndexMapper;
import com.github.howieyoung91.farseer.pojo.DocumentDto;
import com.github.howieyoung91.farseer.pojo.IndexInfo;
import com.github.howieyoung91.farseer.service.DocumentService;
import com.github.howieyoung91.farseer.service.TokenService;
import com.github.howieyoung91.farseer.util.Factory;
import com.github.howieyoung91.farseer.util.StringUtil;
import com.github.howieyoung91.farseer.util.keyword.Keyword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultIndexService extends AbstractIndexService {
    @Resource
    private IndexMapper     indexMapper;
    @Resource
    private DocumentService documentService;
    @Resource
    private TokenService    tokenService;

    // ========================================   public methods   =========================================
    public List<Index> getIndices(String documentId, Page<Index> page) {
        return indexMapper.selectPage(
                Factory.resolvePage(page),
                Factory.createLambdaQueryWrapper(Index.class)
                        .eq(Index::getDocumentId, documentId)
        ).getRecords();
    }

    @Override
    @Transactional
    public Collection<Index> index(List<Document> documents) {
        documentService.insert(documents);
        Collection<Index> indices = buildIndices(documents);
        indexMapper.insertBatch(indices);
        return indices;
    }

    @Override
    public List<DocumentDto> searchQueryWords(String query, Page<Index> page) {
        String[] words = StringUtil.splitByBlank(query);

        Map<String, List<DocumentDto>> filteredDocuments = new HashMap<>();
        Map<String, List<DocumentDto>> searchedDocuments = new HashMap<>();

        searchIndexedDocuments(words, searchedDocuments, filteredDocuments);
        filterDocuments(searchedDocuments, filteredDocuments);
        return collectDocuments(searchedDocuments);
    }


    public List<DocumentDto> searchSingleWord(String word, Page<Index> page) {
        // Token -> Indices -> Documents
        Token token = tokenService.selectToken(word);
        if (token == null) {
            return new ArrayList<>(0);
        }
        List<Index>    indices   = selectIndices(token, page);
        List<Document> documents = documentService.selectIndexedDocuments(indices);

        Map<String, Index> indexMap = indices.stream().collect(Collectors.toMap(Index::getDocumentId, index -> index));
        return convert2DocumentDto(word, documents, indexMap);
    }

    private static List<DocumentDto> convert2DocumentDto(String word, List<Document> documents, Map<String, Index> indexMap) {
        List<DocumentDto> documentDtos = new ArrayList<>();
        for (Document document : documents) {
            Index       index       = indexMap.get(document.getId());
            DocumentDto documentDto = wrapDocument(document, word, index);
            documentDtos.add(documentDto);
        }
        return documentDtos;
    }

    private static DocumentDto wrapDocument(Document document, String word, Index index) {
        DocumentDto documentDto = DocumentDto.wrap(document);
        if (index == null) {
            return documentDto;
        }
        IndexInfo indexInfo = new IndexInfo(index.getCount(), index.getScore());
        documentDto.addIndexInfo(word, indexInfo);
        return documentDto;
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
        List<Index>          indices  = new ArrayList<>();
        Collection<String>   segments = segment(document.getText());
        Map<String, Keyword> keywords = analyze(document.getText(), segments.size() / 2);
        for (String segment : segments) {
            Token token = selectToken(segment);
            Index index = Index.of(token.getId(), document.getId());
            populateIndex(keywords, token, index);
            indices.add(index);
        }
        return indices;
    }

    private static void populateIndex(Map<String, Keyword> keywords, Token token, Index index) {
        index.setScore(calcScore(keywords, token));
    }

    private Token selectToken(String segment) {
        Token token = tokenService.selectToken(segment);
        if (token == null) {
            token = tokenService.insert(segment);
        }
        return token;
    }

    private List<Index> selectIndices(Token token, Page<Index> page) {
        return indexMapper.selectPage(
                        Factory.createPage(page.getCurrent(), page.getSize()),
                        Factory.createLambdaQueryWrapper(Index.class)
                                .eq(Index::getTokenId, token.getId())
                                .orderByDesc(Index::getScore))
                .getRecords();
    }

    private void searchIndexedDocuments(String[] words, Map<String, List<DocumentDto>> searchedDocuments,
                                        Map<String, List<DocumentDto>> filteredDocuments) {
        for (String word : words) {
            List<DocumentDto> documents;
            if (isFilteredWord(word)) {
                word = word.substring(1); // "-csdn" indicates "csdn" is a filtered word
                documents = filteredDocuments.computeIfAbsent(word, ignored -> new ArrayList<>());
            }
            else {
                documents = searchedDocuments.computeIfAbsent(word, ignored -> new ArrayList<>());
            }
            documents.addAll(searchSingleWord(word, new Page<>(1, 1000)));
        }
    }

    private static void filterDocuments(Map<String, List<DocumentDto>> searchedDocuments,
                                        Map<String, List<DocumentDto>> filteredDocuments) {
        searchedDocuments.forEach((word, documentDtos) -> {
            for (List<DocumentDto> filteredDocumentDtos : filteredDocuments.values()) {
                documentDtos.removeAll(filteredDocumentDtos);
            }
        });
    }

    private static List<DocumentDto> collectDocuments(Map<String, List<DocumentDto>> searchedDocuments) {
        ArrayList<DocumentDto> result = new ArrayList<>();
        searchedDocuments.values().forEach(result::addAll);
        return result;
    }

    private static Set<String> findFilteredWord(String[] words) {
        return Arrays.stream(words).filter(DefaultIndexService::isFilteredWord).collect(Collectors.toSet());
    }

    private static boolean isFilteredWord(String word) {
        return word.startsWith("-");
    }
}