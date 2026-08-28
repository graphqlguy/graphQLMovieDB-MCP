package com.graphqlguy.moviedb.mcp;

public final class MovieOperations {

    private MovieOperations() {}

    public static final String RECOMMEND_MOVIES_FOR_MOOD = """
        query RecommendMoviesForMood($mood: Mood!, $excludeWatched: Boolean = false) {
          recommendMoviesForMood(mood: $mood, excludeWatched: $excludeWatched) {
            id
            title
            releaseYear
            genre
            averageRating
          }
        }
        """;

    public static final String SUMMARIZE_MOVIE_REVIEWS = """
        query SummarizeMovieReviews($movieId: ID!) {
          summarizeMovieReviews(movieId: $movieId) {
            movieId
            reviewCount
            summary
            themes
            averageScore
          }
        }
        """;

    public static final String ADD_TO_WATCHLIST = """
        mutation AddToWatchlist($subject: WatchlistSubjectInput!, $status: WatchStatus) {
          addToWatchlist(subject: $subject, status: $status) {
            id
            status
            content {
              title
            }
          }
        }
        """;
}
