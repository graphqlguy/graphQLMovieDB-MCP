package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.config.LatencySimulator;
import com.graphqlguy.moviedb.exception.EntityNotFoundException;
import com.graphqlguy.moviedb.movie.MovieRepository;
import com.graphqlguy.moviedb.tvshow.TvShowRepository;
import com.graphqlguy.moviedb.user.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// A signed-in name that has no row in the users table is a missing user. The
// service reports it as not found and names the unmatched username, so the
// caller can see which name failed to match.
class WatchlistServiceTest {

    @Test
    void findForUser_usernameWithoutMovieDbUser_shouldThrowEntityNotFound() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByUsername("unknown-user")).thenReturn(Optional.empty());
        WatchlistService service = new WatchlistService(
                mock(WatchlistItemRepository.class), mock(MovieRepository.class),
                mock(TvShowRepository.class), userRepository, mock(LatencySimulator.class));

        assertThatThrownBy(() -> service.findForUser("unknown-user"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Entity AppUser not found for username: unknown-user");
    }
}
