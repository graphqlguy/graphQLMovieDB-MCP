package com.graphqlguy.moviedb.recommendation;

import com.graphqlguy.moviedb.movie.Movie;
import com.graphqlguy.moviedb.movie.MovieRepository;
import com.graphqlguy.moviedb.user.AppUser;
import com.graphqlguy.moviedb.watchlist.WatchStatus;
import com.graphqlguy.moviedb.watchlist.WatchlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final MovieRepository movieRepository;
    private final WatchlistItemRepository watchlistRepository;

    public List<Movie> recommendForMood(Mood mood, boolean excludeWatched, AppUser viewer) {
        // The mood is accepted but not yet acted on: this stub returns the catalog
        // reshuffled, deterministic within a 60-second window so a retrying agent
        // sees the same order. A later class wires in a real recommender.
        Set<Long> excludedMovieIds = watchedMovieIds(excludeWatched, viewer);
        long windowSeed = Instant.now().getEpochSecond() / 60;
        Comparator<Movie> stableShuffle = Comparator.comparingInt(
                m -> Long.hashCode(m.getId() ^ windowSeed));
        return movieRepository.findAll().stream()
                .filter(m -> !excludedMovieIds.contains(m.getId()))
                .sorted(stableShuffle)
                .limit(10)
                .toList();
    }

    private Set<Long> watchedMovieIds(boolean excludeWatched, AppUser viewer) {
        // Anonymous viewers (viewer == null) are supported on purpose: the operation
        // is callable without authentication, and an anonymous caller has no
        // watchlist to exclude against.
        if (!excludeWatched || viewer == null) {
            return Set.of();
        }
        return watchlistRepository.findWithContentByUserId(viewer.getId()).stream()
                .filter(item -> item.getStatus() == WatchStatus.WATCHED && item.getMovie() != null)
                .map(item -> item.getMovie().getId())
                .collect(Collectors.toSet());
    }
}