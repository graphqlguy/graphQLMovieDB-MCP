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
            rating
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

    // Class 11: raw material for the summarizer. The summary itself is computed
    // in the tool layer (sampling or the server-side fallback), so the tool
    // reads the reviews rather than the pre-baked summarizeMovieReviews field.
    public static final String MOVIE_REVIEWS = """
        query MovieReviews($movieId: ID!) {
          movie(id: $movieId) {
            reviews {
              score
              comment
            }
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

    // Class 16: the real mutation behind the stage-and-confirm flow.
    public static final String REMOVE_FROM_WATCHLIST = """
        mutation RemoveFromWatchlist($itemId: ID!) {
          removeFromWatchlist(itemId: $itemId)
        }
        """;
}
