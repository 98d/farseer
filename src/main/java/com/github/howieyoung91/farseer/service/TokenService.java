package com.github.howieyoung91.farseer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.config.CacheKeys;
import com.github.howieyoung91.farseer.entity.Token;
import com.github.howieyoung91.farseer.mapper.TokenMapper;
import com.github.howieyoung91.farseer.util.Factory;
import com.github.howieyoung91.farseer.util.PrefixSearcher;
import com.github.howieyoung91.farseer.util.Redis;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {
    @Resource
    private TokenMapper    tokenMapper;
    @Resource
    private Redis          redis;
    @Resource
    private PrefixSearcher prefixSearcher;

    // ========================================   public methods   =========================================

    public Token selectTokenById(String tokenId) {
        Token token = (Token) redis.get(CacheKeys.tokenIdKey(tokenId));
        if (token == null) {
            token = doSelectTokenById(tokenId);
        }
        return token;
    }

    public List<Token> selectTokensById(List<String> tokenIds) {
        // todo cache
        return tokenIds.stream()
                .map(this::selectTokenById)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Token selectTokenByWord(String word) {
        Token token = (Token) redis.get(CacheKeys.tokenWordKey(word));
        if (token == null) {
            token = doSelectTokenByWord(word);
        }
        return token;
    }

    public List<String> selectWordsStartsWith(String prefix, Page<Token> page) {
        return prefixSearcher.searchByPrefix(prefix);
    }

    public List<Token> selectAll() {
        return tokenMapper.selectList(Factory.createLambdaQueryWrapper(Token.class));
    }

    @Transactional
    public Token insert(String word) {
        Token token = Token.fromWord(word);
        tokenMapper.insert(token);
        cacheToken(token);
        return token;
    }

    // ========================================   public methods   =========================================

    /**
     * 在数据库中根据 word 查询 token 并存入缓存
     */
    private Token doSelectTokenByWord(String word) {
        Token token = tokenMapper.selectOne(Factory.createLambdaQueryWrapper(Token.class).eq(Token::getWord, word));
        cacheToken(token);
        return token;
    }

    /**
     * 在数据库中根据 id 查询 token 并存入缓存
     */
    private Token doSelectTokenById(String tokenId) {
        Token token = tokenMapper.selectById(tokenId);
        cacheToken(token);
        return token;
    }

    private void cacheToken(Token token) {
        if (token == null) {
            return;
        }

        redis.kvSet(CacheKeys.tokenIdKey(token.getId()), token);
        redis.kvSet(CacheKeys.tokenWordKey(token.getWord()), token);
    }
}
