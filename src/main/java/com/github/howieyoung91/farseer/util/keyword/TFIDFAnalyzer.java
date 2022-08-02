package com.github.howieyoung91.farseer.util.keyword;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TFIDFAnalyzer {
    private static HashMap<String, Double> IDF_MAP;
    private static HashSet<String>         STOP_WORDS_SET;
    private static double                  IDF_MEDIAN;

    /**
     * tfidf分析方法
     *
     * @param content 需要分析的文本/文档内容
     * @param topN    需要返回的tfidf值最高的N个关键词，若超过 content 本身含有的词语上限数目，则默认返回全部
     */
    public List<Keyword> analyze(String content, int topN) {
        List<Keyword> keywords = new ArrayList<>();
        if (STOP_WORDS_SET == null) {
            STOP_WORDS_SET = new HashSet<>();
            loadStopWords(STOP_WORDS_SET, this.getClass().getResourceAsStream("/jieba/stop_words.txt"));
        }
        if (IDF_MAP == null) {
            IDF_MAP = new HashMap<>();
            loadIDFMap(IDF_MAP, this.getClass().getResourceAsStream("/jieba/idf_dict.txt"));
        }

        Map<String, Double> tfMap = getTF(content);
        for (String word : tfMap.keySet()) {
            // 若该词不在 idf 文档中，则使用平均的 idf 值(可能定期需要对新出现的网络词语进行纳入)
            if (IDF_MAP.containsKey(word)) {
                keywords.add(new Keyword(word, IDF_MAP.get(word) * tfMap.get(word)));
            }
            else {
                keywords.add(new Keyword(word, IDF_MEDIAN * tfMap.get(word)));
            }
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

    /**
     * tf值计算公式
     * tf=N(i,j)/(sum(N(k,j) for all k))
     * N(i,j)表示词语Ni在该文档d（content）中出现的频率，sum(N(k,j))代表所有词语在文档d中出现的频率之和
     */
    private Map<String, Double> getTF(String content) {
        Map<String, Double> tfMap = new HashMap<>();
        if (content == null || content.equals("")) {
            return tfMap;
        }

        JiebaSegmenter       segmenter = new JiebaSegmenter();
        List<String>         segments  = segmenter.sentenceProcess(content);
        Map<String, Integer> freqMap   = new HashMap<>();

        int wordSum = 0;
        for (String segment : segments) {
            // 停用词不予考虑，单字词不予考虑
            if (!STOP_WORDS_SET.contains(segment) && segment.length() > 1) {
                wordSum++;
                if (freqMap.containsKey(segment)) {
                    freqMap.put(segment, freqMap.get(segment) + 1);
                }
                else {
                    freqMap.put(segment, 1);
                }
            }
        }

        // 计算 double 型的 tf值
        for (String word : freqMap.keySet()) {
            tfMap.put(word, freqMap.get(word) * 0.1 / wordSum);
        }

        return tfMap;
    }

    /**
     * 默认jieba分词的停词表
     * url:https://github.com/yanyiwu/nodejieba/blob/master/dict/stop_words.utf8
     */
    private void loadStopWords(Set<String> set, InputStream in) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
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
     * idf值本来需要语料库来自己按照公式进行计算，不过jieba分词已经提供了一份很好的idf字典，所以默认直接使用jieba分词的idf字典
     * url:https://raw.githubusercontent.com/yanyiwu/nodejieba/master/dict/idf.utf8
     */
    private void loadIDFMap(Map<String, Double> map, InputStream in) {
        BufferedReader bufr;
        try {
            bufr = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufr.readLine()) != null) {
                String[] kv = line.trim().split(" ");
                map.put(kv[0], Double.parseDouble(kv[1]));
            }
            try {
                bufr.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // 计算idf值的中位数
            List<Double> idfList = new ArrayList<>(map.values());
            Collections.sort(idfList);
            IDF_MEDIAN = idfList.get(idfList.size() / 2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}

