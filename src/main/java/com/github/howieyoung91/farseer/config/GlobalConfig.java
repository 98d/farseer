package com.github.howieyoung91.farseer.config;

import com.github.howieyoung91.farseer.entity.Token;
import com.github.howieyoung91.farseer.service.TokenService;
import com.github.howieyoung91.farseer.util.PrefixSearcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GlobalConfig {
    @Bean
    PrefixSearcher prefixSearcher(TokenService tokenService) {
        PrefixSearcher searcher = new PrefixSearcher();
        List<Token>    tokens   = tokenService.selectAll();
        for (Token token : tokens) {
            searcher.addWords(token.getWord());
        }
        return searcher;
    }
}
