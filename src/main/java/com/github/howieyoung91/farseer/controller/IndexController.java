package com.github.howieyoung91.farseer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.entity.Index;
import com.github.howieyoung91.farseer.pojo.DocumentDto;
import com.github.howieyoung91.farseer.pojo.DocumentVo;
import com.github.howieyoung91.farseer.pojo.JsonResponse;
import com.github.howieyoung91.farseer.service.DocumentService;
import com.github.howieyoung91.farseer.service.Indexer;
import com.github.howieyoung91.farseer.util.Factory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farseer")
public class IndexController {
    @Resource
    private Indexer         indexService;
    @Resource
    private DocumentService documentService;

    @GetMapping("/document/search/query/{query}")
    public JsonResponse searchByQueryString(@PathVariable String query, Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        List<DocumentDto> documentDtos = indexService.searchByQueryString(query, new Page<>(page, size));
        return JsonResponse.SUCCESSFUL(documentDtos);
    }

    @GetMapping("/document/search/sentence/{sentence}")
    public JsonResponse searchBySentence(@PathVariable String sentence, Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        Collection<DocumentDto> documentDtos = indexService.searchBySentence(sentence, new Page<>(page, size));
        return JsonResponse.SUCCESSFUL(documentDtos);
    }

    @GetMapping("/document/search/word/{word}")
    public JsonResponse searchByWord(@PathVariable String word, Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        List<DocumentDto> documentDtos = indexService.searchByWord(word, new Page<>(page, size));
        return JsonResponse.SUCCESSFUL(documentDtos);
    }

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
        Collection<Index> indices = indexService.getIndices(documentId, Factory.createPage(page, size));
        return JsonResponse.SUCCESSFUL(indices);
    }

    @DeleteMapping("/document/{documentId}/index")
    public JsonResponse deleteIndices(@PathVariable String documentId) {
        int i = indexService.deleteIndices(documentId);
        return JsonResponse.SUCCESSFUL(Map.of("count", i));
    }

    @PutMapping("/index")
    public JsonResponse index(@RequestBody DocumentVo documentVo) {
        Document document = Document.fromJSON(documentVo.getText(), documentVo.getContent());
        document.setHighlight(documentVo.getHighlightPrefix(), documentVo.getHighlightSuffix());
        Collection<Index> index = indexService.index(Collections.singletonList(document));
        return JsonResponse.SUCCESSFUL(index);
    }
}