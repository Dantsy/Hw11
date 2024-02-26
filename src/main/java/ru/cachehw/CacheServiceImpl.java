package ru.cachehw;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class CacheServiceImpl<K, V> implements CacheService<K, V> {
    private final Map<K, V> cache = new HashMap<>();

    public CacheServiceImpl(int maxSize, K timeToLive, ScheduledExecutorService executorService) {
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }
}