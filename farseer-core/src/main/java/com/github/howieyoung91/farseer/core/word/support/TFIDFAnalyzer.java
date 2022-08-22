package com.github.howieyoung91.farseer.core.word.support;

import com.github.howieyoung91.farseer.core.word.Keyword;
import com.github.howieyoung91.farseer.core.word.KeywordAnalyzer;
import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * thread-safe
 */
public class TFIDFAnalyzer implements KeywordAnalyzer {
    private final    String                  stopWordResource;
    private final    String                  idfResource;
    private volatile HashMap<String, Double> idfMap;
    private volatile HashSet<String>         stopWords;
    private          double                  idfMedian;

    public TFIDFAnalyzer(String stopWordResource, String idfResource) throws Exception {
        this.stopWordResource = stopWordResource;
        this.idfResource = idfResource;
        initialize();
    }

    /**
     * tfidf 分析方法
     *
     * @param content 需要分析的文本/文档内容
     * @param topN    需要返回的tfidf值最高的N个关键词，若超过 content 本身含有的词语上限数目，则默认返回全部
     */
    @Override
    public List<Keyword> analyze(String content, int topN) {
        List<Keyword>       keywords = new ArrayList<>();
        Map<String, Double> tfs      = calcTF(content);
        for (Map.Entry<String, Double> entry : tfs.entrySet()) {
            String word = entry.getKey();
            Double tf   = entry.getValue();
            Double idf  = idfMap.getOrDefault(word, idfMedian);
            keywords.add(new Keyword(word, idf * tf)); // calc tf-idf value
        }

        Collections.sort(keywords);

        if (keywords.size() > topN) {
            int num = keywords.size() - topN;
            for (int i = 0; i < num; i++) {
                keywords.remove(topN);
            }
        }
        return keywords;
    }

    private void initialize() throws Exception {
        if (stopWords == null) {
            synchronized (this) {
                if (stopWords == null) {
                    stopWords = new HashSet<>();
                    InputStream stopWord = this.getClass().getResourceAsStream(stopWordResource);
                    loadStopWords(stopWords, stopWord);
                    stopWord.close();
                }
            }
        }
        if (idfMap == null) {
            synchronized (this) {
                if (idfMap == null) {
                    idfMap = new HashMap<>();
                    InputStream stream = this.getClass().getResourceAsStream(idfResource);
                    loadIDFMap(idfMap, stream);
                    stream.close();
                }
            }
        }
    }

    /**
     * tf值计算公式
     * <p>
     * tf=N(i,j) / (sum(N(k,j) for all k))
     * <p>
     * N(i,j) 表示词语Ni在该文档d（content）中出现的频率，sum(N(k,j)) 代表所有词语在文档d中出现的频率之和
     */
    private Map<String, Double> calcTF(String content) {
        Map<String, Double> tfs = new HashMap<>();
        if (content == null || content.equals("")) {
            return tfs;
        }

        JiebaSegmenter       segmenter   = new JiebaSegmenter();
        List<String>         segments    = segmenter.sentenceProcess(content);
        Map<String, Integer> frequencies = new HashMap<>(); // 各个词出现的频数
        int                  total       = 0; // 总词数

        for (String segment : segments) {
            // 停用词不予考虑
            if (stopWords.contains(segment)) {
                continue;
            }
            // 单字词不予考虑
            if (segment.length() > 1) {
                total++;
                Integer frequency = frequencies.get(segment);
                if (frequency == null) {
                    frequency = 0;
                }
                frequencies.put(segment, frequency + 1);
            }
        }

        // 计算 double 型的 tf 值
        for (String word : frequencies.keySet()) {
            tfs.put(word, frequencies.get(word) * 0.1 / total);
        }

        return tfs;
    }

    /**
     * 默认jieba分词的停词表
     * <p>
     * url:https://github.com/yanyiwu/nodejieba/blob/master/dict/stop_words.utf8
     */
    private void loadStopWords(Set<String> set, InputStream in) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                set.add(line.trim());
            }
            try {
                reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * idf值本来需要语料库来自己按照公式进行计算，不过 jieba 分词已经提供了一份很好的idf字典，所以默认直接使用jieba分词的 idf 字典
     * <p>
     * url:https://raw.githubusercontent.com/yanyiwu/nodejieba/master/dict/idf.utf8
     */
    private void loadIDFMap(Map<String, Double> map, InputStream in) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] kv = line.trim().split(" ");
                map.put(kv[0], Double.parseDouble(kv[1]));
            }
            reader.close();

            // 计算 idf 值的中位数
            List<Double> idfs = new ArrayList<>(map.values());
            Collections.sort(idfs);
            idfMedian = idfs.get(idfs.size() / 2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
