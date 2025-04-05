package event_booking_system.demo.services;

import org.springframework.stereotype.Service;

@Service
public interface BaseRedisService<K, F, V>{
    void set(K key, V value);

    void setTimeToLive(K key, long timeoutInDays);

    void hashSet(K key, F field, V value);

    V get(K key);
}
