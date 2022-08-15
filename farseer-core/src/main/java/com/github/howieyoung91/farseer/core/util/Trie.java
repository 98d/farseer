package com.github.howieyoung91.farseer.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Trie {
    private final Node          root   = new Node();
    private final AtomicInteger height = new AtomicInteger(0);

    private static class Node {
        Map<Character, Node> children = new ConcurrentHashMap<>();
        boolean              isWord   = false;
    }

    public void addWords(String... words) {
        for (String word : words) {
            addWord(word);
        }
    }

    public void addWord(String word) {
        Node curr   = root;
        int  length = word.length();
        for (int i = 0; i < length; i++) {
            char c = word.charAt(i);
            curr = curr.children.computeIfAbsent(c, ignored -> new Node());
        }
        curr.isWord = true;

        // 先简单地比较一下 如果比 maxWordLength 小 就没必要写入了 尽可能地避免 cas
        if (length > height.get()) {
            height.getAndUpdate(oldLength -> Math.max(length, oldLength)); // 强行把最新值写入
        }
    }

    public boolean contains(String word) {
        Node curr   = root;
        int  length = word.length();
        for (int i = 0; i < length; i++) {
            char c    = word.charAt(i);
            Node next = curr.children.get(c);
            if (next == null) {
                return false;
            }
            curr = next;
        }
        return curr.isWord;
    }

    public int height() {
        return height.get();
    }

    public List<String> getWords(String prefix, int count) {
        List<String> words = new ArrayList<>();
        if (prefix.length() > height.get()) {
            return words;
        }

        Node curr   = root;
        int  length = prefix.length();
        for (int i = 0; i < length; i++) {
            char c    = prefix.charAt(i);
            Node next = curr.children.get(c);
            if (next == null) {
                return words;
            }
            curr = next;
        }

        resolve(prefix, curr, new StringBuilder(), words, count);
        return words;
    }

    private void resolve(String prefix, Node curr, StringBuilder builder, Collection<String> words, int count) {
        if (curr.isWord) {
            if (words.size() < count) {
                words.add(prefix + builder.toString());
            }
            else {
                return;
            }
        }
        for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
            builder.append(entry.getKey());
            curr = entry.getValue();
            resolve(prefix, curr, builder, words, count);
            builder.deleteCharAt(builder.length() - 1);
        }
    }
}