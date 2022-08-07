package com.github.howieyoung91.farseer.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Token implements Serializable {
    private String id;
    private String word;

    public static Token fromWord(String word) {
        Token token = new Token();
        token.setWord(word);
        return token;
    }
}
