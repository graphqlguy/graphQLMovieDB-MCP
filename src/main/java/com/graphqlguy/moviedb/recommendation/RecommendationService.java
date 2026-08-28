package com.graphqlguy.moviedb.recommendation;

import com.graphqlguy.moviedb.movie.Movie;
import com.graphqlguy.moviedb.movie.MovieRepository;
import com.graphqlguy.moviedb.user.AppUser;
import com.graphqlguy.moviedb.watchlist.WatchStatus;
import com.graphqlguy.moviedb.watchlist.WatchlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // Each mood maps to a genre set plus rating and runtime constraints in
        // MoodProfile. Results rank by rating, highest first, with a stable
        // tiebreak on id so the order does not change between calls.
        MoodProfile profile = MoodProfile.forMood(mood);
        Set<Long> excludedMovieIds = watchedMovieIds(excludeWatched, viewer);
        return movieRepository.findAll().stream()
                .filter(profile::matches)
                .filter(m -> !excludedMovieIds.contains(m.getId()))
                .sorted(Comparator.comparingDouble(Movie::getRating).reversed()
                        .thenComparing(Movie::getId))
                .limit(10)
                .toList();
    }

    private Set<Long> watchedMovieIds(boolean excludeWatched, AppUser viewer) {
        // Anonymous viewers (viewer == null) are supported on purpose: the operation
        // is callable without authentication, and an anonymous caller does not have a
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
