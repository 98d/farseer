package com.github.howieyoung91.farseer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.pojo.DocumentDto;
import com.github.howieyoung91.farseer.entity.Index;

import java.util.Collection;
import java.util.List;

/**
 * @author Howie Young
 */
public interface Indexer {
    /**
     * 查询 document 的倒排索引
     */
    Collection<Index> getIndices(String documentId, Page<Index> page);

    /**
     * 删除倒排索引
     */
    int deleteIndices(String documentId);

    /**
     * 把 documents 添加进入倒排索引库
     */
    Collection<Index> index(List<Document> documents);

    /**
     * 根据单个词进行查询 (不分词)
     */
    List<DocumentDto> searchByWord(String word, Page<Index> page);

    /**
     * 根据一个句子进行查询 (智能分词)
     * <p>
     * example:
     * <p>
     * [0] JavaC++Golang -> Java/C++/Golang
     * <p>
     * [1] Java是一门面向对象的编程语言 -> Java/是/一门/面向对象/的/编程语言
     */
    Collection<DocumentDto> searchBySentence(String sentence, Page<Index> page);

    /**
     * 根据查询字符串进行查询 (空格分词模式)
     * <p>
     * examples:
     * <p>
     * [0] Java C++ -> 表示查询 Java 和 C++ 然后取交集
     * <p>
     * [1] Java -Golang -> 表示查询 Java 和 Golang，并做差集 Java-Golang
     * <p>
     *
     * @param query 查询字符串
     * @param page  分页
     */
    List<DocumentDto> searchByQueryString(String query, Page<Index> page);
}
