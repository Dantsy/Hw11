package ru.cachehw;

import org.springframework.web.bind.annotation.*;

@RestController
public class CacheController {

    private final CacheService<String, Object> cacheService;

    public CacheController(CacheService<String, Object> cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/cache/{key}")
    public Object getFromCache(@PathVariable String key) {
        return cacheService.get(key);
    }

    @PutMapping("/cache/{key}")
    public void putInCache(@PathVariable String key, @RequestBody Object value) {
        cacheService.put(key, value);
    }

    @DeleteMapping("/cache/{key}")
    public void removeFromCache(@PathVariable String key) {
        cacheService.remove(key);
    }
}