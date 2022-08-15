package com.github.howieyoung91.farseer.core.util;

import java.util.Objects;

public class Interval {
    private int    start;
    private int    end;
    private String word;

    public Interval(int start, int end, String word) {
        this.start = start;
        this.end = end;
        this.word = word;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public String word() {
        return word;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "[" + start + "," + end + "," + word + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Interval interval = (Interval) o;
        return start == interval.start && end == interval.end && Objects.equals(word, interval.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, word);
    }
}
