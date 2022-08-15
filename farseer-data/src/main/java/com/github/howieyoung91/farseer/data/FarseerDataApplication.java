package com.github.howieyoung91.farseer.data;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class FarseerDataApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(FarseerDataApplication.class)
                .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
                .run(args);
    }
}
