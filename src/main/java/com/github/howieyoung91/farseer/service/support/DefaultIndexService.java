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
import com.github.howieyoung91.farseer.util.Highlighter;
import com.github.howieyoung91.farseer.util.StringUtil;
import com.github.howieyoung91.farseer.util.keyword.Keyword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
    @Override
    public List<Index> getIndices(String documentId, Page<Index> page) {
        return indexMapper.selectPage(
                        Factory.resolvePage(page),
                        Factory.createLambdaQueryWrapper(Index.class)
                                .eq(Index::getDocumentId, documentId))
                .getRecords();
    }

    @Override
    @Transactional
    public int deleteIndices(String documentId) {
        documentService.deleteById(documentId);
        return indexMapper.delete(Factory.createLambdaQueryWrapper(Index.class).eq(Index::getDocumentId, documentId));
    }

    @Override
    @Transactional
    public Collection<Index> index(List<Document> documents) {
        documentService.insert(documents);
        Collection<Index> indices = buildIndices(documents);
        insertIndices(indices);
        return indices;
    }

    @Override
    public List<DocumentDto> searchByWord(String word, Page<Index> page) {
        // Token -> Indices -> Documents
        Token          token     = tokenService.selectToken(word);
        List<Index>    indices   = selectIndicesByToken(token, page);
        List<Document> documents = documentService.selectDocumentsByIndices(indices);

        highlight(word, documents);

        return convert2DocumentDto(word, documents, indices);
    }


    @Override
    public Collection<DocumentDto> searchBySentence(String sentence, Page<Index> page) {
        List<String> words = segmentOnSearchMode(sentence);

        Map<String, DocumentDto> hitDocumentDtoMap = new HashMap<>(); // documentId : documentDto
        for (String word : words) {
            selectDocumentIndexedByWord(word, page, hitDocumentDtoMap);
        }

        return hitDocumentDtoMap.values();
    }

    @Override
    public List<DocumentDto> searchByQueryString(String query, Page<Index> page) {
        String[] words = StringUtil.splitByBlank(query);

        Map<String, DocumentDto> hitDocumentDtoMap   = new HashMap<>(); // documentId : documentDto
        Set<String>              filteredDocumentIds = new HashSet<>();

        // search documents
        for (String word : words) {
            String         resolvedWord = resolveWord(word);
            List<Document> documents;
            if (isFilteredWord(word)) {
                documents = selectDocumentIndexedByWord(resolvedWord, page, null);
                documents.stream().map(Document::getId).forEach(filteredDocumentIds::add); // add into filteredDocumentIds waiting for using
            }
            else {
                selectDocumentIndexedByWord(resolvedWord, page, hitDocumentDtoMap);
            }
        }

        return filterDocuments(hitDocumentDtoMap, filteredDocumentIds);
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
        List<String>         segments = segmentOnIndexMode(document.getText());
        Map<String, Keyword> keywords = analyze(document.getText(), segments.size());
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
        index.setCount(1);
    }

    private Token selectToken(String segment) {
        Token token = tokenService.selectToken(segment);
        if (token == null) {
            token = tokenService.insert(segment);
        }
        return token;
    }

    private void insertIndices(Collection<Index> indices) {
        for (Index index : indices) {
            insertIndex(index);
        }
    }

    private void insertIndex(Index index) {
        Index existedIndex = indexMapper.selectOne(Factory.createLambdaQueryWrapper(Index.class)
                .eq(Index::getTokenId, index.getTokenId())
                .eq(Index::getDocumentId, index.getDocumentId()));
        if (existedIndex == null) {
            indexMapper.insert(index);
        }
        else {
            existedIndex.setCount(existedIndex.getCount() + 1);
            indexMapper.updateById(existedIndex);
        }
    }

    private List<Index> selectIndicesByToken(Token token, Page<Index> page) {
        if (token == null) {
            return new ArrayList<>(0);
        }
        return indexMapper.selectPage(
                        Factory.resolvePage(page),
                        Factory.createLambdaQueryWrapper(Index.class)
                                .eq(Index::getTokenId, token.getId())
                                .orderByDesc(Index::getScore))
                .getRecords();
    }

    private static List<DocumentDto> convert2DocumentDto(String word, List<Document> documents, List<Index> indices) {
        Map<String, Index> indexMap     = indices.stream().collect(Collectors.toMap(Index::getDocumentId, index -> index));
        List<DocumentDto>  documentDtos = new ArrayList<>();
        for (Document document : documents) {
            DocumentDto documentDto = DocumentDto.from(document);
            populateIndexInfo(word, documentDto, indexMap);
            documentDtos.add(documentDto);
        }
        return documentDtos;
    }

    /**
     * 处理某个单词
     * -csdn -> csdn
     * java -> java
     */
    private static String resolveWord(String word) {
        // "-csdn" indicates "csdn" is a filtered word
        return (isFilteredWord(word) ? word.substring(1) : word).toLowerCase(Locale.ENGLISH);
    }

    private static boolean isFilteredWord(String word) {
        return word.startsWith("-");
    }

    /**
     * 根据一个 word 查询对应的 document
     *
     * @param word              一个单词
     * @param page              分页
     * @param hitDocumentDtoMap documentId : documentDto 如果这个哈希表被传入，将会把查出来的 document 转为 documentDto 并添加进哈希表
     * @return 查询出来的 document
     */
    private List<Document> selectDocumentIndexedByWord(String word, Page<Index> page, Map<String, DocumentDto> hitDocumentDtoMap) {
        // Token -> Index -> Document
        Token          token     = tokenService.selectToken(word);
        List<Index>    indices   = selectIndicesByToken(token, page);
        List<Document> documents = documentService.selectDocumentsByIndices(indices);
        if (hitDocumentDtoMap != null) {
            resolveHitDocumentDtoMap(word, hitDocumentDtoMap, indices, documents);
        }
        return documents;
    }

    private static void resolveHitDocumentDtoMap(String word, Map<String, DocumentDto> hitDocumentDtoMap,
                                                 List<Index> indices, List<Document> documents) {
        Map<String, Index> indexMap = indices.stream().collect(Collectors.toMap(Index::getDocumentId, index -> index));

        // 把 document 转为 documentId 并取交集
        if (hitDocumentDtoMap.isEmpty()) {
            // 第一次不用取交集，因为还是第一次还是空集
            for (Document document : documents) {
                DocumentDto documentDto = DocumentDto.from(document);
                populateIndexInfo(word, documentDto, indexMap);
                hitDocumentDtoMap.put(document.getId(), documentDto);
            }
        }
        else {
            intersect(hitDocumentDtoMap, documents);

            for (DocumentDto documentDto : hitDocumentDtoMap.values()) {
                populateIndexInfo(word, documentDto, indexMap);
            }
        }
    }

    private static void intersect(Map<String, DocumentDto> hitDocumentDtoMap, List<Document> documents) {
        // documentId : document
        Map<String, Document> collect = documents.stream().collect(Collectors.toMap(Document::getId, document -> document));
        doIntersect(hitDocumentDtoMap, collect);
    }

    /**
     * 取交集
     */
    private static void doIntersect(Map<String, DocumentDto> hitDocumentDtoMap, Map<String, Document> documentMap) {
        hitDocumentDtoMap.keySet().removeIf(documentId -> !documentMap.containsKey(documentId));
    }

    private static List<DocumentDto> filterDocuments(Map<String, DocumentDto> hitDocumentDtoMap, Set<String> filteredDocumentIds) {
        return hitDocumentDtoMap.values().stream()
                .filter(documentDto -> !filteredDocumentIds.contains(documentDto.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 向 documentDto 中添加命中的索引信息 IndexInfo
     *
     * @param word        该索引的 tokenId 对应的 word
     * @param documentDto a documentDto
     * @param indexMap    一个哈希表 documentId : index，记录查询出 document 所使用的 index
     */
    private static void populateIndexInfo(String word, DocumentDto documentDto, Map<String, Index> indexMap) {
        Index index = indexMap.get(documentDto.getId());
        if (index != null) {
            documentDto.addIndexInfo(word, IndexInfo.from(index));
        }
    }

    private static void highlight(String word, List<Document> documents) {
        for (Document document : documents) {
            Highlighter highlighter     = new Highlighter(document.getText(), document.getHighlightPrefix(), document.getHighlightSuffix());
            String      highlightedText = highlighter.highlight(word);
            document.setText(highlightedText);
        }
    }
}