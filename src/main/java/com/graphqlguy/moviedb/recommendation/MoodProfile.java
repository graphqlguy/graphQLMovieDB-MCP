package com.graphqlguy.moviedb.recommendation;

import com.graphqlguy.moviedb.movie.Movie;
import com.graphqlguy.moviedb.shared.Genre;

import java.util.Map;
import java.util.Set;

/**
 * Maps a mood onto the catalogue's genre vocabulary plus quality and length
 * constraints. Moods are cross-genre by nature, which is why this is a set and
 * not a single genre: COMFORT is a tone, not a category.
 */
public record MoodProfile(Set<Genre> genres, double minRating, Integer maxRuntime) {

    private static final Map<Mood, MoodProfile> PROFILES = Map.of(
        Mood.COMFORT,    new MoodProfile(Set.of(Genre.COMEDY, Genre.ROMANCE, Genre.FANTASY), 7.0, 140),
        Mood.ADVENTURE,  new MoodProfile(Set.of(Genre.ACTION, Genre.FANTASY, Genre.SCIFI, Genre.WESTERN), 6.5, null),
        Mood.ROMANCE,    new MoodProfile(Set.of(Genre.ROMANCE, Genre.DRAMA), 6.5, null),
        Mood.HORROR,     new MoodProfile(Set.of(Genre.HORROR, Genre.THRILLER, Genre.MYSTERY), 6.0, null),
        Mood.THOUGHTFUL, new MoodProfile(Set.of(Genre.DRAMA, Genre.SCIFI, Genre.WAR, Genre.MYSTERY, Genre.CRIME), 7.5, null),
        Mood.COMEDY,     new MoodProfile(Set.of(Genre.COMEDY, Genre.ROMANCE), 6.0, null));

    public static MoodProfile forMood(Mood mood) {
        return PROFILES.get(mood);
    }

    public boolean matches(Movie movie) {
        if (movie.getGenre() == null || !genres.contains(movie.getGenre())) {
            return false;
        }
        if (movie.getRating() == null || movie.getRating() < minRating) {
            return false;
        }
        return maxRuntime == null || movie.getRuntime() == null || movie.getRuntime() <= maxRuntime;
    }
}
