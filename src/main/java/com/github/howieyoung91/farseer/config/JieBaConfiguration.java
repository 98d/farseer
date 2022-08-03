package com.github.howieyoung91.farseer.config;

import com.github.howieyoung91.farseer.util.keyword.TFIDFAnalyzer;
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
        return new TFIDFAnalyzer();
    }
}
