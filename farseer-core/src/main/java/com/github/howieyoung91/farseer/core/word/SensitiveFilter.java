package com.github.howieyoung91.farseer.core.word;

/**
 * @author Howie Young on <i>2022/08/16 18:26<i/>
 * @version 1.0
 * @since 1.0
 */
public interface SensitiveFilter {
    String filter(String raw);

    boolean isSensitive(String word);
}
