package com.github.howieyoung91.farseer.core.word;

import java.util.List;

/**
 * @author Howie Young on <i>2022/08/17 12:35<i/>
 * @version 1.0
 * @since 1.0
 */
public interface KeywordAnalyzer {
    List<Keyword> analyze(String content, int topN);
}
