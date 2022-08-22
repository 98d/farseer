package com.github.howieyoung91.farseer.core.config;

import com.github.howieyoung91.farseer.core.word.support.TFIDFAnalyzer;
import com.huaban.analysis.jieba.JiebaSegmenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JieBaConfiguration {
    @Bean
    JiebaSegmenter segmenter() {
        return new JiebaSegmenter();
    }

    @Bean
    TFIDFAnalyzer tfidfAnalyzer() {
        try {
            return new TFIDFAnalyzer("/jieba/stop_words_new.txt", "/jieba/idf_dict.txt");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
