package com.github.howieyoung91.farseer.service.support;

import com.github.howieyoung91.farseer.entity.Token;
import com.github.howieyoung91.farseer.service.IndexService;
import com.github.howieyoung91.farseer.util.keyword.Keyword;
import com.github.howieyoung91.farseer.util.keyword.TFIDFAnalyzer;
import com.huaban.analysis.jieba.JiebaSegmenter;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractIndexService implements IndexService {
    @Resource
    private JiebaSegmenter segmenter;
    @Resource
    private TFIDFAnalyzer  analyzer;

    /**
     * 对一段文本进行分词
     */
    protected Collection<String> segment(String text) {
        return new HashSet<>(segmenter.sentenceProcess(text));
    }

    /**
     * 分析一段文本中的关键词
     */
    protected Map<String, Keyword> analyze(String text, int number) {
        List<Keyword> keywords = analyzer.analyze(text, number);
        return keywords.stream().collect(Collectors.toMap(Keyword::getName, keyword -> keyword));
    }

    /**
     * 计算某个 token 的 score
     */
    protected static double calcScore(Map<String, Keyword> keywords, Token token) {
        Keyword keyword = keywords.get(token.getWord());
        return keyword == null ? 0 : keyword.getTfidfvalue();
    }
}
