package com.github.howieyoung91.farseer.service;

import com.github.howieyoung91.farseer.entity.Token;
import com.github.howieyoung91.farseer.mapper.TokenMapper;
import com.github.howieyoung91.farseer.util.Factory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TokenService {
    @Resource
    private TokenMapper tokenMapper;

    public Token selectToken(String word) {
        return tokenMapper.selectOne(Factory.createLambdaQueryWrapper(Token.class).eq(Token::getWord, word));
    }

    public Token insert(String word) {
        Token token = new Token();
        token.setWord(word);
        tokenMapper.insert(token);
        return token;
    }
}
