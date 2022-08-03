package com.github.howieyoung91.farseer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.entity.Index;
import com.github.howieyoung91.farseer.pojo.DocumentDto;
import com.github.howieyoung91.farseer.service.IndexService;
import com.github.howieyoung91.farseer.util.Factory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/farseer/")
public class IndexController {
    @Resource
    private IndexService indexService;

    @GetMapping("/query/{keyword}")
    public List<DocumentDto> search(@PathVariable String keyword, Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        return indexService.searchQueryWords(keyword, new Page<>(page, size));
    }


    @GetMapping("/document/{documentId}/index")
    public List<Index> getIndices(@PathVariable String documentId, Integer page, Integer size) {
        if (documentId == null) {
            return Collections.emptyList();
        }
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 1000;
        }
        return indexService.getIndices(documentId, Factory.createPage(page, size));
    }

    @PutMapping("/index")
    public int index(String text, String content) {
        Document          document = Document.fromJSON(text, content);
        Collection<Index> index    = indexService.index(Collections.singletonList(document));
        return index.size();
    }
}