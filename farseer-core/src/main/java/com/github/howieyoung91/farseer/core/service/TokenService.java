package com.github.howieyoung91.farseer.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.howieyoung91.farseer.core.config.CacheKeys;
import com.github.howieyoung91.farseer.core.entity.Token;
import com.github.howieyoung91.farseer.core.mapper.TokenMapper;
import com.github.howieyoung91.farseer.core.util.Factory;
import com.github.howieyoung91.farseer.core.util.PrefixSearcher;
import com.github.howieyoung91.farseer.core.util.Redis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TokenService {
    @Resource
    private TokenMapper    tokenMapper;
    @Resource
    private Redis          redis;
    @Resource
    private PrefixSearcher prefixSearcher;

    // ========================================   public methods   =========================================

    public Token selectTokenById(String tokenId) {
        if (!existsDocumentByTokenId(tokenId)) {
            return null;
        }
        String cacheKey = CacheKeys.tokenIdKey(tokenId);
        Token  token    = (Token) redis.get(cacheKey);
        if (token == null) {
            token = doSelectTokenById(tokenId);
        }
        return token;
    }

    public List<Token> selectTokensById(List<String> tokenIds) {
        return tokenIds.stream()
                .map(this::selectTokenById)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Token selectTokenByWord(String word) {
        if (!existsDocumentByWord(word)) {
            return null;
        }
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

        cacheToken(token.getId(), word, token);
        return token;
    }

    // ========================================   public methods   =========================================

    /**
     * 查看布谷鸟过滤器
     */
    private boolean existsDocumentByTokenId(String tokenId) {
        return !lookupCuckooFilter(tokenId, CacheKeys.tokenIdCuckooFilter());
    }

    private boolean existsDocumentByWord(String word) {
        return !lookupCuckooFilter(word, CacheKeys.tokenWordCuckooFilter());
    }

    private boolean lookupCuckooFilter(String key, String cuckooKey) {
        if (key == null) {
            return false;
        }
        return redis.cfexists(cuckooKey, key);
    }

    /**
     * 在数据库中根据 id 查询 token 并存入缓存
     */
    private Token doSelectTokenById(String tokenId) {
        Token token = tokenMapper.selectById(tokenId);
        cacheToken(tokenId, null, token);
        return token;
    }

    /**
     * 在数据库中根据 word 查询 token 并存入缓存
     */
    private Token doSelectTokenByWord(String word) {
        Token token = tokenMapper.selectOne(
                Factory.createLambdaQueryWrapper(Token.class)
                        .eq(Token::getWord, word));
        cacheToken(null, word, token);
        return token;
    }

    private void cacheToken(String cacheTokenId, String cachedWord, Token cachedToken) {
        cacheTokenId(cacheTokenId, cachedToken);
        cacheTokenWord(cachedWord, cachedToken);
    }

    private void cacheTokenWord(String cachedWord, Token token) {
        if (token == null) {
            cuckooAdd(CacheKeys.tokenWordCuckooFilter(), cachedWord);
            return;
        }
        cuckooDel(CacheKeys.tokenWordCuckooFilter(), token.getWord());
        kvSet(CacheKeys.tokenWordKey(token.getWord()), token);
    }

    private void cacheTokenId(String cachedTokenId, Token token) {
        if (token == null) {
            cuckooAdd(CacheKeys.tokenIdCuckooFilter(), cachedTokenId);
            return;
        }
        cuckooDel(CacheKeys.tokenIdCuckooFilter(), token.getId());
        kvSet(CacheKeys.tokenIdKey(token.getId()), token);
    }

    private void cuckooAdd(String cuckooFilterKey, String value) {
        if (value == null) {
            return;
        }
        log.info("cuckoo filter [{}] add key [{}]", cuckooFilterKey, value);
        redis.cfadd(cuckooFilterKey, value);
    }

    private void cuckooDel(String cuckooFilterKey, String value) {
        if (value == null) {
            return;
        }
        if (redis.cfexists(cuckooFilterKey, value)) {
            log.info("cuckoo filter {} del key {}", cuckooFilterKey, value);
            redis.cfdel(cuckooFilterKey, value);
        }
    }

    private void kvSet(String key, Token token) {
        log.info("redis key add: {} - {}", key, token);
        redis.kvSet(key, token);
    }
}
