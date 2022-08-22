package com.github.howieyoung91.farseer.core.util;

import com.github.howieyoung91.farseer.core.config.redis.CfCommands;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.dynamic.RedisCommandFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class Redis {
    @Resource
    RedisClient redisClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void kvSet(final String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void kvSet(final String key, Object value, long expireTime) {
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MILLISECONDS);
    }

    public void del(final String key) {
        redisTemplate.delete(key);
    }

    public boolean exist(final String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void expire(final String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void hashMapPut(final String key, final String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object hashMapGet(final String key, final String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public void lPush(final String key, Object... value) {
        redisTemplate.opsForList().leftPushAll(key, value);
    }

    public Object lPop(final String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public void rPush(final String key, Object... value) {
        redisTemplate.opsForList().rightPushAll(key, value);
    }

    public Object rPop(final String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    public List<Object> listRangeGet(final String key, long startIndex, long endIndex) {
        return redisTemplate.opsForList().range(key, startIndex, endIndex);
    }

    public void setAdd(final String key, Object... value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public Set<Object> setGet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public void zadd(final String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public long zcard(final String key) {
        Long count = redisTemplate.opsForZSet().zCard(key);
        return count == null ? 0 : count;
    }

    public Set<Object> zrange(final String key, long startIndex, long endIndex) {
        return redisTemplate.opsForZSet().range(key, startIndex, endIndex);
    }

    public Set<Object> zrevrange(final String key, long startIndex, long endIndex) {
        return redisTemplate.opsForZSet().reverseRange(key, startIndex, endIndex);
    }

    public Set<Object> zsetRangeGetByScore(final String key, double startScore, double endScore) {
        return redisTemplate.opsForZSet().rangeByScore(key, startScore, endScore);
    }

    public <T> T exec(ScriptSource scriptSource, Class<T> returnType, List<String> keys, Object... args) {
        DefaultRedisScript<T> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(returnType);
        redisScript.setScriptSource(scriptSource);
        return exec(redisScript, keys, args);
    }

    public <T> T exec(RedisScript<T> redisScript, List<String> keys, Object[] args) {
        return redisTemplate.execute(redisScript, keys, args);
    }

    public Cursor<ZSetOperations.TypedTuple<Object>> zscan(String key, int count, String match) {
        return redisTemplate.opsForZSet().scan(key, ScanOptions.scanOptions().match(match).count(count).build());
    }

    public Cursor<ZSetOperations.TypedTuple<Object>> zscan(String key, int count) {
        return redisTemplate.opsForZSet().scan(key, ScanOptions.scanOptions().count(count).build());
    }

    // todo 连接池
    public void cfadd(String key, Object value) {
        try (StatefulRedisConnection<String, String> connect = getConnection()) {
            CfCommands commands = getCfCommands(connect);
            commands.add(key, value);
        }
    }

    public void cfdel(String key, Object value) {
        try (StatefulRedisConnection<String, String> connect = getConnection()) {
            CfCommands commands = getCfCommands(connect);
            commands.del(key, value);
        }
    }

    public boolean cfexists(String key, Object value) {
        try (StatefulRedisConnection<String, String> connect = getConnection()) {
            CfCommands commands = getCfCommands(connect);
            return (long) commands.exists(key, value).get(0) == 1;
        }
    }

    private StatefulRedisConnection<String, String> getConnection() {
        return redisClient.connect();
    }

    private RedisCommandFactory getCommandFactory(StatefulRedisConnection<String, String> connect) {
        return new RedisCommandFactory(connect);
    }

    private CfCommands getCfCommands(StatefulRedisConnection<String, String> connect) {
        return getCommandFactory(connect).getCommands(CfCommands.class);
    }
}