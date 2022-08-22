package com.github.howieyoung91.farseer.core.word.support;

import com.github.howieyoung91.farseer.core.util.AcAutomation;
import com.github.howieyoung91.farseer.core.word.Highlighter;
import com.github.howieyoung91.farseer.core.word.Interval;

import java.util.Iterator;
import java.util.List;

public class AcHighlighter implements Highlighter {
    private       String       prefix;
    private       String       suffix;
    private final AcAutomation ac;

    public AcHighlighter(String prefix, String suffix, String... words) {
        AcAutomation.Builder builder = AcAutomation.Builder.aAcAutomation();
        for (String word : words) {
            builder.addWords(word.toLowerCase());
        }
        ac = builder.build();

        this.prefix = (prefix == null ? "" : prefix);
        this.suffix = (suffix == null ? "" : suffix);
    }

    @Override
    public String highlight(String text) {
        if (!shouldHighlight()) {
            return text;
        }

        List<Interval> intervals = ac.search(text);
        if (intervals.isEmpty()) {
            return text;
        }

        mergeIntervals(intervals); // 保证没有重叠区间

        return doHighlight(text, intervals);
    }

    private boolean shouldHighlight() {
        return !prefix.equals("") || !suffix.equals("");
    }

    private String doHighlight(String text, List<Interval> intervals) {
        Iterator<Interval> iterator = intervals.iterator();
        StringBuilder      builder  = new StringBuilder();
        Interval           interval = iterator.next();

        for (int i = 0; i < text.length(); i++) {
            if (interval != null) {
                if (i == interval.start()) {
                    builder.append(prefix);
                }
                else if (i == interval.end()) {
                    builder.append(suffix);
                    if (iterator.hasNext()) {
                        interval = iterator.next();
                    }
                    else {
                        interval = null;
                    }
                }
            }

            builder.append(text.charAt(i));
        }
        if (interval != null) {
            builder.append(suffix);
        }
        return builder.toString();
    }

    /**
     * 合并重叠区间
     */
    private static void mergeIntervals(List<Interval> intervals) {
        /*
         * +-----+
         * +-----+
         *    +-------+
         *    +-------+
         *     ⬇
         * +----------+
         * +----------+
         */
        if (intervals.size() > 1) {
            Iterator<Interval> it   = intervals.iterator();
            Interval           curr = it.next();
            Interval           next;
            do {
                next = it.next();
                if (curr.end() >= next.start()) {
                    it.remove();
                    curr.setWord(curr.word().concat(next.word()));
                    curr.setEnd(curr.end() + next.word().length());
                }
                else {
                    curr = next;
                }
            } while (it.hasNext());
        }
    }
}
