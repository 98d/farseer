package com.github.howieyoung91.farseer.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.core.entity.Document;
import com.github.howieyoung91.farseer.core.entity.Index;
import com.github.howieyoung91.farseer.core.pojo.DocumentDto;
import com.github.howieyoung91.farseer.core.pojo.DocumentVo;
import com.github.howieyoung91.farseer.core.pojo.JsonResponse;
import com.github.howieyoung91.farseer.core.service.DocumentService;
import com.github.howieyoung91.farseer.core.service.Indexer;
import com.github.howieyoung91.farseer.core.util.Factory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farseer")
public class DefaultIndexController implements IndexController {
    @Resource
    private Indexer         indexer;
    @Resource
    private DocumentService documentService;

    @Override
    @GetMapping("/document/search/query/{query}")
    public JsonResponse searchByQueryString(@PathVariable String query, Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        List<DocumentDto> documentDtos = indexer.searchByQueryString(query, new Page<>(page, size));
        return JsonResponse.SUCCESSFUL(documentDtos);
    }

    @Override
    @GetMapping("/document/search/sentence/{sentence}")
    public JsonResponse searchBySentence(@PathVariable String sentence, Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        Collection<DocumentDto> documentDtos = indexer.searchBySentence(sentence, new Page<>(page, size));
        return JsonResponse.SUCCESSFUL(documentDtos);
    }

    @Override
    @GetMapping("/document/search/word/{word}")
    public JsonResponse searchByWord(@PathVariable String word, Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        List<DocumentDto> documentDtos = indexer.searchByWord(word, new Page<>(page, size));
        return JsonResponse.SUCCESSFUL(documentDtos);
    }

    @Override
    @GetMapping("/document/{documentId}/index")
    public JsonResponse getIndices(@PathVariable String documentId, Integer page, Integer size) {
        if (documentId == null) {
            return JsonResponse.SUCCESSFUL(Collections.emptyList());
        }
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 1000;
        }
        Collection<Index> indices = indexer.getIndices(documentId, Factory.createPage(page, size));
        return JsonResponse.SUCCESSFUL(indices);
    }

    @Override
    @DeleteMapping("/document/{documentId}/index")
    public JsonResponse deleteIndices(@PathVariable String documentId) {
        int i = indexer.deleteIndices(documentId);
        return JsonResponse.SUCCESSFUL(Map.of("count", i));
    }

    @Override
    @PutMapping("/index")
    public JsonResponse index(@RequestBody DocumentVo documentVo) {
        Document document = Document.fromJSON(documentVo.getText(), documentVo.getContent());
        document.setHighlight(documentVo.getHighlightPrefix(), documentVo.getHighlightSuffix());
        Collection<Index> index = indexer.index(Collections.singletonList(document));
        return JsonResponse.SUCCESSFUL(index);
    }
}