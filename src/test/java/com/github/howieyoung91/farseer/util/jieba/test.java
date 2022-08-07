package com.github.howieyoung91.farseer.util.jieba;

import com.github.howieyoung91.farseer.util.keyword.Keyword;
import com.github.howieyoung91.farseer.util.keyword.TFIDFAnalyzer;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;


@Slf4j
public class test {
    private JiebaSegmenter segmenter = new JiebaSegmenter();
    private TFIDFAnalyzer  analyzer  = new TFIDFAnalyzer("/jieba/stop_words.txt", "/jieba/idf_dict.txt");

    String[] sentences = new String[]{"美沃可视数码裂隙灯,检查眼前节健康状况", "欧美夏季ebay连衣裙 气质圆领通勤绑带收腰连衣裙 zc3730"};

    public test() throws Exception {}

    @Test
    public void segTest1() {
        List<SegToken> process = segmenter.process("没有这些加加减减", JiebaSegmenter.SegMode.SEARCH);
        System.out.println(process);
    }

    @Test
    public void tfidfTest1() {
        String content = "Java c++";

        List<SegToken> tokens = segmenter.process(content, JiebaSegmenter.SegMode.SEARCH);
        System.out.println(tokens);

        List<Keyword> keywords = analyzer.analyze(content, tokens.size());
        System.out.println(keywords.toString());
    }
}
