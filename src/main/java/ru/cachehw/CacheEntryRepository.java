package ru.cachehw;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CacheEntryRepository extends JpaRepository<CacheEntry, String> {
}