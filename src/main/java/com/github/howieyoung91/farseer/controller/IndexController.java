package com.github.howieyoung91.farseer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.entity.Index;
import com.github.howieyoung91.farseer.service.IndexService;
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

    @GetMapping("/{keyword}")
    public List<Document> search(@PathVariable String keyword, Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        return indexService.search(keyword, new Page<>(page, size));
    }

    @PutMapping("/index")
    public int index(String text, String content) {
        Document          document = Document.fromJSON(text, content);
        Collection<Index> index    = indexService.index(Collections.singletonList(document));
        return index.size();
    }
}