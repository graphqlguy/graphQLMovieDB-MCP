package com.graphqlguy.moviedb.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.graphqlguy.moviedb.country.Country;
import com.graphqlguy.moviedb.tmdb.CommunityRating;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Cache<Integer, CommunityRating> tmdbRatingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(500)
                .build();
    }

    @Bean
    public Cache<String, Country> countryCache() {
        // Country data is effectively static; cache generously so person lists
        // don't refetch the same code from the external API on every request.
        return Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .maximumSize(300)
                .build();
    }

    /**
     * Sixty seconds so the effect is visible inside one class. A production
     * deployment would use hours, because a summary only changes when the
     * review set does.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("reviewSummaries");
        manager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(60)));
        return manager;
    }
}
