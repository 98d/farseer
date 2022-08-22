package com.github.howieyoung91.farseer.core.word;

import java.util.Objects;

public class Keyword implements Comparable<Keyword> {
    private final double tfidf;
    private final String name;

    public Keyword(String name, double tfidf) {
        this.name = name;
        this.tfidf = (double) Math.round(tfidf * 10000) / 10000; // tfidf值只保留3位小数
    }

    public double getTFIDF() {
        return tfidf;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[" + name + ":" + tfidf + "]";
    }

    @Override
    public int compareTo(Keyword o) {
        return this.tfidf - o.tfidf > 0 ? -1 : 1;
    }

    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        long temp = Double.doubleToLongBits(tfidf);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Keyword keyword = (Keyword) o;
        return Double.compare(keyword.tfidf, tfidf) == 0 && Objects.equals(name, keyword.name);
    }
}

