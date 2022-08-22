package com.github.howieyoung91.farseer.core.service.support;

import com.github.howieyoung91.farseer.core.entity.Token;
import com.github.howieyoung91.farseer.core.service.Indexer;
import com.github.howieyoung91.farseer.core.word.Keyword;
import com.github.howieyoung91.farseer.core.word.support.TFIDFAnalyzer;
import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SegmentCapableIndexer implements Indexer {
    @Resource
    private JiebaSegmenter segmenter;
    @Resource
    private TFIDFAnalyzer  analyzer;

    protected List<String> segmentOnSearchMode(String text) {
        return segment(text, JiebaSegmenter.SegMode.SEARCH);
    }

    /**
     * 对一段文本进行分词
     */
    protected List<String> segmentOnIndexMode(String text) {
        return segment(text, JiebaSegmenter.SegMode.INDEX);
    }

    private List<String> segment(String text, JiebaSegmenter.SegMode mode) {
        return segmenter.process(text, mode).stream()
                .filter(segToken -> StringUtils.isNotBlank(segToken.word)) // 去除空白文本
                .map(segToken -> segToken.word)
                .collect(Collectors.toList());
    }

    /**
     * 分析一段文本中的关键词
     */
    protected Map<String, Keyword> analyze(String text, int number) {
        List<Keyword> keywords = analyzer.analyze(text, number);
        return keywords.stream().collect(Collectors.toMap(keyword -> keyword.getName().toLowerCase(Locale.ENGLISH), keyword -> keyword, (a, b) -> b));
    }

    /**
     * 计算某个 token 的 score
     */
    protected static double calcScore(Map<String, Keyword> keywords, Token token) {
        Keyword keyword = keywords.get(token.getWord());
        return keyword == null ? 0 : keyword.getTFIDF();
    }
}
