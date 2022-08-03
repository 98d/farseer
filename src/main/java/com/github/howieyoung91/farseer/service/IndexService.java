package com.github.howieyoung91.farseer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.pojo.DocumentDto;
import com.github.howieyoung91.farseer.entity.Index;

import java.util.Collection;
import java.util.List;

public interface IndexService {
    List<Index> getIndices(String documentId, Page<Index> page);

    /**
     * 把 documents 添加进入索引库
     */
    Collection<Index> index(List<Document> documents);

    /**
     * 根据单个词进行查询
     */
    List<DocumentDto> searchSingleWord(String word, Page<Index> page);

    /**
     * 根据查询字符串进行查询
     * <p>
     * collection arraylist -> 表示查询 collection 和 arraylist 然后取并集
     * <p>
     * collection -arraylist -> 表示查询 collection 和 arraylist，并对其做差集 collection-arraylist
     * <p>
     *
     * @param query 查询字符串
     * @param page  分页
     */
    List<DocumentDto> searchQueryWords(String query, Page<Index> page);
}
