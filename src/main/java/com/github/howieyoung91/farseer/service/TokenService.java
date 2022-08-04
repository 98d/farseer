package com.github.howieyoung91.farseer.service;

import com.github.howieyoung91.farseer.entity.Token;
import com.github.howieyoung91.farseer.mapper.TokenMapper;
import com.github.howieyoung91.farseer.util.Factory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class TokenService {
    @Resource
    private TokenMapper tokenMapper;

    public Token selectToken(String word) {
        return tokenMapper.selectOne(Factory.createLambdaQueryWrapper(Token.class).eq(Token::getWord, word));
    }

    public Token selectTokenById(String tokenId) {
        if (tokenId == null) {
            return null;
        }
        return tokenMapper.selectById(tokenId);
    }

    public List<Token> selectTokensById(List<String> tokenIds) {
        if (tokenIds.isEmpty()) {
            return new ArrayList<>(0);
        }
        return tokenMapper.selectBatchIds(tokenIds);
    }

    public Token insert(String word) {
        Token token = new Token();
        token.setWord(word);
        tokenMapper.insert(token);
        return token;
    }
}
