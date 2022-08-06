package com.github.howieyoung91.farseer.util;

import java.util.*;

/**
 * AC自动机
 * 用于实现字符串的多模匹配
 */
public class AcAutomation {
    private static class Node {
        Character            character;
        Map<Character, Node> children = new HashMap<>(4);
        Node                 fail;
        Map<Character, Node> fails    = new HashMap<>();
        private boolean isWord = false;
        private int     level  = 0;

        public Node(Character character) {
            this.character = character;
        }

        public void addChild(Character c, Node node) {
            children.put(c, node);
        }

        public Node child(Character c) {
            return children.get(c);
        }

        public boolean hasChild() {
            return !children.isEmpty();
        }

        public boolean hasChild(Character c) {
            return extracted(c);
        }

        private boolean extracted(Character c) {
            return children.containsKey(c);
        }

        public Node fail(Character c) {
            return fails.get(c);
        }

        public void addFail(Character c, Node node) {
            fails.put(c, node);
        }

        public boolean hasFail() {
            return !fails.isEmpty();
        }

        public boolean hasFail(Character c) {
            return fails.containsKey(c);
        }

        @Override
        public String toString() {
            return String.valueOf(character);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Node node = (Node) o;
            return Objects.equals(character, node.character) && Objects.equals(children, node.children) && Objects.equals(fail, node.fail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, children, fail);
        }
    }

    private final Node        root  = new Node(null);
    private final Set<String> words = new HashSet<>();

    public AcAutomation() {
        root.fail = root;
    }

    public static AcAutomation from(String... words) {
        AcAutomation ac = new AcAutomation();
        for (String word : words) {
            if (ac.words.add(word)) {
                ac.addWord(word);
            }
        }
        ac.buildFail();
        return ac;
    }

    public List<Interval> search(String text) {
        ArrayList<Interval> intervals = new ArrayList<>();
        Node                curr      = root;
        int                 length    = text.length();
        for (int i = 0; i < length; ) {
            char c = Character.toLowerCase(text.charAt(i)); // 不区分大小写

            // determine child
            Node child;
            while (true) {
                child = curr.child(c);
                if (child != null) {
                    break;
                }
                curr = curr.fail;
                if (curr == root) {
                    child = root;
                    if (!root.hasChild(c)) {
                        // 根结点不存在 则跳过
                        i++;
                    }
                    break;
                }
            }

            curr = child;
            // 匹配成功
            if (curr != root) {
                if (curr.isWord) {
                    int start = i - curr.level + 1;
                    int end   = i + 1;
                    intervals.add(new Interval(start, end, text.substring(start, end)));
                }
                i++;
            }
        }
        return intervals;
    }

    public Set<String> words() {
        return words;
    }

    private void addWord(String word) {
        Node curr   = root;
        int  length = word.length();
        for (int i = 0; i < length; i++) {
            char c = word.charAt(i);
            curr = curr.children.computeIfAbsent(c, character -> new Node(c));
        }
        curr.level = word.length();
        curr.isWord = true;
    }

    private void words(Node curr, StringBuilder builder, ArrayList<String> result) {
        if (!curr.hasChild()) {
            String word = builder.toString();
            result.add(word);
        }
        for (Node child : curr.children.values()) {
            builder.append(child.character);
            words(child, builder, result);
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    /**
     * 构建失配指针
     */
    private void buildFail() {
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(root); // init

        while (!queue.isEmpty()) {
            Node node = queue.removeLast();

            node.children.forEach((character, child) -> {
                // determine fail pointer
                Node fail = node.fail.child(character);
                if (fail == null || fail == child) {
                    fail = root;
                }
                child.fail = fail;
                queue.add(child);
            });
        }
    }

    public static class Builder {
        private final AcAutomation ac = new AcAutomation();

        private Builder() {}

        public static Builder aAcAutomation() {
            return new Builder();
        }

        public Builder addWords(String... words) {
            for (String word : words) {
                ac.addWord(word);
            }
            return this;
        }

        public AcAutomation build() {
            ac.buildFail();
            return ac;
        }
    }
}
