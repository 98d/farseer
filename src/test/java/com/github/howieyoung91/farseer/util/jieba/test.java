package com.github.howieyoung91.farseer.util.jieba;

import com.github.howieyoung91.farseer.util.keyword.Keyword;
import com.github.howieyoung91.farseer.util.keyword.TFIDFAnalyzer;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;


@Slf4j
public class test {
    private JiebaSegmenter segmenter = new JiebaSegmenter();
    private TFIDFAnalyzer  analyzer  = new TFIDFAnalyzer();

    String[] sentences = new String[]{"美沃可视数码裂隙灯,检查眼前节健康状况", "欧美夏季ebay连衣裙 气质圆领通勤绑带收腰连衣裙 zc3730"};

    @Test
    public void segTest1() {
        for (String sentence : sentences) {
            List<SegToken> tokens = segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH);
            System.out.printf(Locale.getDefault(), "\n%s\n%s", sentence, tokens.toString());
            System.out.println(tokens);
            TFIDFAnalyzer analyzer = new TFIDFAnalyzer();
            List<Keyword> keywords = analyzer.analyze(sentence, 10);
            System.out.println(keywords);
        }
    }

    @Test
    public void tfidfTest1() {
        String content = "Java是一门面向对象的编程语言，不仅吸收了C++语言的各种优点，还摒弃了C++里难以理解的多继承、指针等概念，因此Java语言具有功能强大和简单易用两个特征。Java语言作为静态面向对象编程语言的代表，极好地实现了面向对象理论，允许程序员以优雅的思维方式进行复杂的编程 Java具有简单性、面向对象、分布式、健壮性、安全性、平台独立与可移植性、多线程、动态性等特点。Java可以编写桌面应用程序、Web应用程序、分布式系统和嵌入式系统应用程序等";

        List<SegToken> tokens = segmenter.process(content, JiebaSegmenter.SegMode.SEARCH);
        // System.out.println(tokens);

        List<Keyword> keywords = analyzer.analyze(content, tokens.size());
        System.out.println(keywords.toString());
    }
}
