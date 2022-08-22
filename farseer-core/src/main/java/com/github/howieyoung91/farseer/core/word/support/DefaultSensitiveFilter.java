package com.github.howieyoung91.farseer.core.word.support;

import com.github.howieyoung91.farseer.core.util.AcAutomation;
import com.github.howieyoung91.farseer.core.word.Interval;
import com.github.howieyoung91.farseer.core.word.SensitiveFilter;

import java.util.List;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/16 18:24]
 */
public class DefaultSensitiveFilter implements SensitiveFilter {
    private final AcAutomation ac;
    private final String       replacement;

    public DefaultSensitiveFilter(AcAutomation ac) {
        this(ac, "*");
    }

    public DefaultSensitiveFilter(AcAutomation ac, String replacement) {
        this.ac = ac;
        this.replacement = replacement;
    }

    @Override
    public String filter(String raw) {
        List<Interval> intervals = ac.search(raw);
        if (intervals.isEmpty()) {
            return raw;
        }
        return doFilter(raw, intervals);
    }

    @Override
    public boolean isSensitive(String word) {
        return ac.containsWord(word);
    }

    private String doFilter(String raw, List<Interval> intervals) {
        StringBuilder builder = new StringBuilder(raw);
        Interval      l       = intervals.get(0);
        Interval      curr    = l, last;
        for (int i = 1; i < intervals.size(); i++) {
            curr = intervals.get(i);
            last = intervals.get(i - 1);
            if (curr.start() > last.end()) {
                int length = last.end() - l.start(); // calc length
                builder.replace(l.start(), last.end(), "*".repeat(length));
                l = curr;
            }
        }
        int length = curr.end() - l.start();
        builder.replace(l.start(), curr.end(), replacement.repeat(length));
        return builder.toString();
    }

    public static final class Builder {
        private AcAutomation.Builder acBuilder   = AcAutomation.Builder.aAcAutomation();
        private String               replacement = "*";

        private Builder() {}

        public static Builder aFilter() {
            return new Builder();
        }

        public Builder addWords(String... words) {
            acBuilder.addWords(words);
            return this;
        }

        public Builder replacement(String replacement) {
            this.replacement = replacement;
            return this;
        }

        public DefaultSensitiveFilter build() {
            return new DefaultSensitiveFilter(acBuilder.build(), replacement);
        }
    }
}
