package event_booking_system.demo.services.impls;

import event_booking_system.demo.services.BaseRedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BaseRedisServiceImpl<K, F, V> implements BaseRedisService<K, F, V> {

    RedisTemplate<K, V> redisTemplate;
    HashOperations<K, F, V> hashOperations;
    @Override
    public void set(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setTimeToLive(K key, long timeoutInDays) {
        redisTemplate.expire(key, timeoutInDays, TimeUnit.MILLISECONDS);
    }

    @Override
    public void hashSet(K key, F field, V value) { // add insert plenty of field-value into hash without override(unless field duplicated)
        hashOperations.put(key, field, value);
    }

    @Override
    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }
}
