package com.graphqlguy.moviedb.recommendation;

import com.graphqlguy.moviedb.movie.Movie;
import com.graphqlguy.moviedb.user.AppUser;
import com.graphqlguy.moviedb.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @QueryMapping
    List<Movie> recommendMoviesForMood(
        @Argument Mood mood,
        @Argument boolean excludeWatched,
        Principal principal
    ) {
        AppUser viewer = resolveViewer(principal);
        return recommendationService.recommendForMood(mood, excludeWatched, viewer);
    }

    private AppUser resolveViewer(Principal principal) {
        if (principal == null || "anonymousUser".equals(principal.getName())) {
            return null;
        }
        return userRepository.findByUsername(principal.getName()).orElse(null);
    }
}
