package com.github.howieyoung91.farseer.core.config;

import com.github.howieyoung91.farseer.core.word.support.DefaultSensitiveFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/16 19:18]
 */
@Configuration
public class SensitiveWordsConfig {
    @Bean
    DefaultSensitiveFilter filter() {
        URL resource = getClass().getResource("/sensitive/sensitive.txt");
        Objects.requireNonNull(resource);
        DefaultSensitiveFilter.Builder builder = DefaultSensitiveFilter.Builder.aFilter();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(resource.getFile(), StandardCharsets.UTF_8));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.addWords(line);
            }
            reader.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }
}
