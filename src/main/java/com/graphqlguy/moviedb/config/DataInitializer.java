package com.graphqlguy.moviedb.config;

import com.graphqlguy.moviedb.movie.Movie;
import com.graphqlguy.moviedb.movie.MovieCast;
import com.graphqlguy.moviedb.movie.MovieCastRepository;
import com.graphqlguy.moviedb.movie.MovieRepository;
import com.graphqlguy.moviedb.person.Person;
import com.graphqlguy.moviedb.person.PersonRepository;
import com.graphqlguy.moviedb.review.Review;
import com.graphqlguy.moviedb.review.ReviewRepository;
import com.graphqlguy.moviedb.tvshow.TvShow;
import com.graphqlguy.moviedb.tvshow.TvShowCast;
import com.graphqlguy.moviedb.tvshow.TvShowCastRepository;
import com.graphqlguy.moviedb.tvshow.TvShowRepository;
import com.graphqlguy.moviedb.tvshow.Episode;
import com.graphqlguy.moviedb.tvshow.EpisodeRepository;
import com.graphqlguy.moviedb.user.AppUser;
import com.graphqlguy.moviedb.user.Role;
import com.graphqlguy.moviedb.user.UserRepository;
import com.graphqlguy.moviedb.shared.Genre;
import com.graphqlguy.moviedb.watchlist.WatchStatus;
import com.graphqlguy.moviedb.watchlist.WatchlistItem;
import com.graphqlguy.moviedb.watchlist.WatchlistItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final MovieRepository movieRepo;
    private final PersonRepository personRepo;
    private final MovieCastRepository movieCastRepo;
    private final TvShowRepository tvShowRepo;
    private final TvShowCastRepository tvShowCastRepo;
    private final EpisodeRepository episodeRepo;
    private final UserRepository userRepo;
    private final ReviewRepository reviewRepo;
    private final WatchlistItemRepository watchlistRepo;
    private final PasswordEncoder passwordEncoder;

    private static final String BASE = "https://image.tmdb.org/t/p/w500/";

    private record CastEntry(Person person, String characterName) {}


    @Bean
    CommandLineRunner initData() {
        return args -> {
            if (movieRepo.count() == 0) {
                log.info("Seeding database with movies, actors, directors, and TV shows...");

            // ── Users ─────────────────────────────────────────────────────
            AppUser admin = userRepo.save(AppUser.builder().username("admin").email("admin@moviedb.com")
                .password(passwordEncoder.encode("admin123")).role(Role.ADMIN).build());
            AppUser user = userRepo.save(AppUser.builder().username("user").email("user@moviedb.com")
                .password(passwordEncoder.encode("user123")).role(Role.USER).build());
            AppUser mara    = userRepo.save(AppUser.builder().username("mara").email("mara@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());
            AppUser petra   = userRepo.save(AppUser.builder().username("petra").email("petra@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());
            AppUser dan     = userRepo.save(AppUser.builder().username("dan").email("dan@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());
            AppUser ines    = userRepo.save(AppUser.builder().username("ines").email("ines@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());
            AppUser tomas   = userRepo.save(AppUser.builder().username("tomas").email("tomas@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());
            AppUser sofia   = userRepo.save(AppUser.builder().username("sofia").email("sofia@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());
            AppUser henrik  = userRepo.save(AppUser.builder().username("henrik").email("henrik@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());
            AppUser lucia   = userRepo.save(AppUser.builder().username("lucia").email("lucia@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());
            AppUser noor    = userRepo.save(AppUser.builder().username("noor").email("noor@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());
            AppUser viktor  = userRepo.save(AppUser.builder().username("viktor").email("viktor@example.com")
                    .password(passwordEncoder.encode("password")).role(Role.USER).build());

            // ── Persons (directors) ───────────────────────────────────────
            Person christopherNolan = personRepo.save(Person.builder().name("Christopher Nolan").birthYear(1970).countryCode("GB")
                    .biography("Master of mind-bending large-scale narratives.").photoUrl(BASE + "xuAIuYSmsUzKlUMBFGVZaWsY3DZ.jpg").build());
            Person davidFincher = personRepo.save(Person.builder().name("David Fincher").birthYear(1962).countryCode("US")
                    .biography("Master of dark psychological thrillers.").photoUrl(BASE + "tpEczFclQZeKAiCeKZZ0adRvtfz.jpg").build());
            Person martinScorsese = personRepo.save(Person.builder().name("Martin Scorsese").birthYear(1942).countryCode("US")
                    .biography("Legendary filmmaker of crime epics and character studies.").photoUrl(BASE + "9U9Y5GQuWX3EZy39B8nkk4NY01S.jpg").build());
            Person francisFordCoppola = personRepo.save(Person.builder().name("Francis Ford Coppola").birthYear(1939).countryCode("US")
                    .biography("Director of The Godfather trilogy.").photoUrl(BASE + "IwGgkmW6IoJ9vuNF0T9CU3FYUX.jpg").build());
            Person stanleyKubrick = personRepo.save(Person.builder().name("Stanley Kubrick").birthYear(1928).countryCode("US")
                    .biography("Perfectionist visionary known for genre-defining films.").photoUrl(BASE + "yFT0VyIelI9aegZrsAwOG5iVP4v.jpg").build());
            Person ridleyScott = personRepo.save(Person.builder().name("Ridley Scott").birthYear(1937).countryCode("GB")
                    .biography("Versatile director of sci-fi, epics and thrillers.").photoUrl(BASE + "zABJmN9opmqD4orWl3KSdCaSo7Q.jpg").build());
            Person denisVilleneuve = personRepo.save(Person.builder().name("Denis Villeneuve").birthYear(1967).countryCode("CA")
                    .biography("Modern master of cerebral science fiction.").photoUrl(BASE + "zdDx9Xs93UIrJFWYApYR28J8M6b.jpg").build());
            Person jamesCameron = personRepo.save(Person.builder().name("James Cameron").birthYear(1954).countryCode("CA")
                    .biography("Technical pioneer known for blockbusters.").photoUrl(BASE + "9NAZnTjBQ9WcXAQEzZpKy4vdQto.jpg").build());
            Person peterJackson = personRepo.save(Person.builder().name("Peter Jackson").birthYear(1961).countryCode("NZ")
                    .biography("Director of the Lord of the Rings trilogy.").photoUrl(BASE + "bNc908d59Ba8VDNr4eCcm4G1cR.jpg").build());
            Person frankDarabont = personRepo.save(Person.builder().name("Frank Darabont").birthYear(1959).countryCode("US")
                    .biography("Director of two of Stephen King's greatest adaptations.").photoUrl(BASE + "oQvVLXw8Sh7gDww3g1jr0UY0FFj.jpg").build());
            Person robertZemeckis = personRepo.save(Person.builder().name("Robert Zemeckis").birthYear(1951).countryCode("US")
                    .biography("Known for innovative VFX and heartfelt stories.").photoUrl(BASE + "lPYDQ5LYNJ12rJZENtyASmVZ1Ql.jpg").build());
            Person johnMcTiernan = personRepo.save(Person.builder().name("John McTiernan").birthYear(1951).countryCode("US")
                    .biography("Action director known for Die Hard.").photoUrl(BASE + "yVfDkVbgQHD1A7JSV8Z47EjB1mU.jpg").build());
            Person sidneyLumet = personRepo.save(Person.builder().name("Sidney Lumet").birthYear(1924).countryCode("US")
                    .biography("Director of intense character dramas.").photoUrl(BASE + "hjj3V2DkPJ46zo5uz9bsZQzAk6R.jpg").build());
            Person josephKosinski = personRepo.save(Person.builder().name("Joseph Kosinski").birthYear(1974).countryCode("US")
                    .biography("Director of Top Gun: Maverick.").photoUrl(BASE + "oWLUXWY0j8TYzwnf2wETYWO181S.jpg").build());
            Person anthonyRusso = personRepo.save(Person.builder().name("Anthony Russo").birthYear(1970).countryCode("US")
                    .biography("Co-director of Avengers: Endgame.").photoUrl(BASE + "xbINBnWn28YygYWUJ1aSAw0xPRv.jpg").build());
            Person joeRusso = personRepo.save(Person.builder().name("Joe Russo").birthYear(1971).countryCode("US")
                    .biography("Co-director of Avengers: Endgame.").photoUrl(BASE + "o0OXjFzL10jCy89iAs7UzzSbyoK.jpg").build());
            Person lanaWachowski = personRepo.save(Person.builder().name("Lana Wachowski").birthYear(1965).countryCode("US")
                    .biography("Co-creator of the Matrix franchise.").photoUrl(BASE + "5KuRHnoH8UkSCFHMKf4YjKOvzOM.jpg").build());
            Person lillyWachowski = personRepo.save(Person.builder().name("Lilly Wachowski").birthYear(1967).countryCode("US")
                    .biography("Co-creator of the Matrix franchise.").photoUrl(BASE + "rCScAjSpeKA19BLNR07MqNNeeTT.jpg").build());
            Person sergioLeone = personRepo.save(Person.builder().name("Sergio Leone").birthYear(1929).countryCode("IT")
                    .biography("Master of the Spaghetti Western genre.").photoUrl(BASE + "2576qoW8l9Z1nKGM10ar60aIwUu.jpg").build());
            Person georgeLucas = personRepo.save(Person.builder().name("George Lucas").birthYear(1944).countryCode("US")
                    .biography("Creator of Star Wars and Indiana Jones.").photoUrl(BASE + "mDLDvsx8PaZoEThkBdyaG1JxPdf.jpg").build());
            Person irvinKershner = personRepo.save(Person.builder().name("Irvin Kershner").birthYear(1923).countryCode("US")
                    .biography("Director of The Empire Strikes Back.").photoUrl(BASE + "imtFUtcASoh2e1Emtt62UuFkIWA.jpg").build());
            Person richardMarquand = personRepo.save(Person.builder().name("Richard Marquand").birthYear(1937).countryCode("GB")
                    .biography("Director of Return of the Jedi.").photoUrl(BASE + "eEalDQpLsXJqejPDQ3MWGe95UHT.jpg").build());
            Person jJAbrams = personRepo.save(Person.builder().name("J.J. Abrams").birthYear(1966).countryCode("US")
                    .biography("Director of Star Wars: The Force Awakens and The Rise of Skywalker.").photoUrl(BASE + "k4IWd2RV5kY1kAL2VgKQwFvnCLP.jpg").build());
            Person rianJohnson = personRepo.save(Person.builder().name("Rian Johnson").birthYear(1973).countryCode("US")
                    .biography("Director of The Last Jedi and Knives Out.").photoUrl(BASE + "ggwlJvCn0laNGjcvwGchuwC00hQ.jpg").build());
            Person jonathanMostow = personRepo.save(Person.builder().name("Jonathan Mostow").birthYear(1961).countryCode("US")
                    .biography("Director of Terminator 3: Rise of the Machines.").photoUrl(BASE + "yRMYvjGLIf0aOUKVLnU6jSpR1oQ.jpg").build());
            Person mcg = personRepo.save(Person.builder().name("McG").birthYear(1968).countryCode("US")
                    .biography("Director of Terminator Salvation.").photoUrl(BASE + "sEcoHVCqc2IrJkxgixGHrDytsyd.jpg").build());
            Person alanTaylor = personRepo.save(Person.builder().name("Alan Taylor").birthYear(1959).countryCode("US")
                    .biography("Director of Terminator Genisys.").photoUrl(BASE + "sXC2wNRo7lshghNnNaPdWQ9sqKe.jpg").build());
            Person timMiller = personRepo.save(Person.builder().name("Tim Miller").birthYear(1964).countryCode("US")
                    .biography("Director of Deadpool and Terminator: Dark Fate.").photoUrl(BASE + "dCyBYwhO76j5wA96HPb6k5xk2Le.jpg").build());
            Person rennyHarlin = personRepo.save(Person.builder().name("Renny Harlin").birthYear(1959).countryCode("FI")
                    .biography("Action director known for Die Hard 2.").photoUrl(BASE + "IhXiDrZBrsLZpB5K5BmzUGkp4G.jpg").build());
            Person lenWiseman = personRepo.save(Person.builder().name("Len Wiseman").birthYear(1973).countryCode("US")
                    .biography("Director of Live Free or Die Hard.").photoUrl(BASE + "aXsTPBb6dQ2T3PxLqy2ijf2nxrG.jpg").build());
            Person johnMoore = personRepo.save(Person.builder().name("John Moore").birthYear(1970).countryCode("IE")
                    .biography("Director of A Good Day to Die Hard.").photoUrl(BASE + "n3czTYjeuJHaTLvMHllnT35uhnF.jpg").build());
            Person jonathanDemme = personRepo.save(Person.builder().name("Jonathan Demme").birthYear(1944).countryCode("US")
                    .biography("Director of The Silence of the Lambs.").photoUrl(BASE + "fb3TfFITlOC0BN3kNpUXj1FL0LN.jpg").build());
            Person michaelCurtiz = personRepo.save(Person.builder().name("Michael Curtiz").birthYear(1886).countryCode("HU")
                    .biography("Director of Casablanca.").photoUrl(BASE + "AnxPuEsdjPTJ6uIaHY0KdgBeu7t.jpg").build());
            Person alfredHitchcock = personRepo.save(Person.builder().name("Alfred Hitchcock").birthYear(1899).countryCode("GB")
                    .biography("Master of suspense and psychological thrillers.").photoUrl(BASE + "108fiNM6poRieMg7RIqLJRxdAwG.jpg").build());
            Person johnKrasinski = personRepo.save(Person.builder().name("John Krasinski").birthYear(1979).countryCode("US")
                    .biography("Actor-director known for A Quiet Place.").photoUrl(BASE + "6YauDiiTBwRGC1xnwspPmNvPWUu.jpg").build());
            Person davidCrane = personRepo.save(Person.builder().name("David Crane").birthYear(1957).countryCode("US")
                    .biography("Co-creator and showrunner of Friends.").photoUrl(BASE + "1NYo5ZYCSqoxQ5sqXLMDm3cqvKp.jpg").build());
            Person martaKauffman = personRepo.save(Person.builder().name("Marta Kauffman").birthYear(1956).countryCode("US")
                    .biography("Co-creator and showrunner of Friends.").photoUrl(BASE + "AsX4ZOoQP5oQVLiA51zdRiTNKTm.jpg").build());
            Person larryDavid = personRepo.save(Person.builder().name("Larry David").birthYear(1947).countryCode("US")
                    .biography("Co-creator and showrunner of Seinfeld.").photoUrl(BASE + "ojPx93eaDcanOVi4AH14uAFwXhn.jpg").build());
            Person davidBenioff = personRepo.save(Person.builder().name("David Benioff").birthYear(1970).countryCode("US")
                    .biography("Co-creator of Game of Thrones.").photoUrl(BASE + "xvNN5huL0X8yJ7h3IZfGG4O2zBD.jpg").build());
            Person dBWeiss = personRepo.save(Person.builder().name("D.B. Weiss").birthYear(1971).countryCode("US")
                    .biography("Co-creator of Game of Thrones.").photoUrl(BASE + "6Wt006TIQoDSSnl0YaKihfn3w7K.jpg").build());

            // ── Persons (actors) ──────────────────────────────────────────
            Person tomHanks = personRepo.save(Person.builder().name("Tom Hanks").birthYear(1956).countryCode("US")
                    .biography("Versatile actor known for dramatic and comedic roles.").photoUrl(BASE + "eKF1sGJRrZJbfBG1KirPt1cfNd3.jpg").build());
            Person leonardoDiCaprio = personRepo.save(Person.builder().name("Leonardo DiCaprio").birthYear(1974).countryCode("US")
                    .biography("Acclaimed actor known for intense performances.").photoUrl(BASE + "qZs7xVpe2gGXfGaS5NRkhjPOedW.jpg").build());
            Person morganFreeman = personRepo.save(Person.builder().name("Morgan Freeman").birthYear(1937).countryCode("US")
                    .biography("Iconic actor with a distinctive voice.").photoUrl(BASE + "jPsLqiYGSofU4s6BjrxnefMfabb.jpg").build());
            Person robertDeNiro = personRepo.save(Person.builder().name("Robert De Niro").birthYear(1943).countryCode("US")
                    .biography("Legendary method actor.").photoUrl(BASE + "cT8htcckIuyI1Lqwt1CvD02ynTh.jpg").build());
            Person alPacino = personRepo.save(Person.builder().name("Al Pacino").birthYear(1940).countryCode("US")
                    .biography("Iconic for passionate and intense portrayals.").photoUrl(BASE + "m8HAAjq1T75JypKk0v1FFQn4ysZ.jpg").build());
            Person bradPitt = personRepo.save(Person.builder().name("Brad Pitt").birthYear(1963).countryCode("US")
                    .biography("Award-winning actor and producer.").photoUrl(BASE + "cckcYc2v0yh1tc9QjRelptcOBko.jpg").build());
            Person christianBale = personRepo.save(Person.builder().name("Christian Bale").birthYear(1974).countryCode("GB")
                    .biography("Known for extreme physical transformations.").photoUrl(BASE + "7Pxez9J8fuPd2Mn9kex13YALrCQ.jpg").build());
            Person matthewMcConaughey = personRepo.save(Person.builder().name("Matthew McConaughey").birthYear(1969).countryCode("US")
                    .biography("Oscar-winning actor.").photoUrl(BASE + "lCySuYjhXix3FzQdS4oceDDrXKI.jpg").build());
            Person nataliePortman = personRepo.save(Person.builder().name("Natalie Portman").birthYear(1981).countryCode("IL")
                    .biography("Oscar-winning actress.").photoUrl(BASE + "edPU5HxncLWa1YkgRPNkSd68ONG.jpg").build());
            Person keanuReeves = personRepo.save(Person.builder().name("Keanu Reeves").birthYear(1964).countryCode("CA")
                    .biography("Action star known for physicality.").photoUrl(BASE + "8RZLOyYGsoRe9p44q3xin9QkMHv.jpg").build());
            Person samuelLJackson = personRepo.save(Person.builder().name("Samuel L. Jackson").birthYear(1948).countryCode("US")
                    .biography("One of the highest-grossing actors of all time.").photoUrl(BASE + "AiAYAqwpM5xmiFrAIeQvUXDCVvo.jpg").build());
            Person marlonBrando = personRepo.save(Person.builder().name("Marlon Brando").birthYear(1924).countryCode("US")
                    .biography("Revolutionary actor who transformed American cinema.").photoUrl(BASE + "iyO183LVAJ0I4ZkNibINPjfAjCP.jpg").build());
            Person liamNeeson = personRepo.save(Person.builder().name("Liam Neeson").birthYear(1952).countryCode("IE")
                    .biography("Dramatic actor turned action star.").photoUrl(BASE + "g0iIEyt9ILiKTG0g8K69US5VtLy.jpg").build());
            Person tomCruise = personRepo.save(Person.builder().name("Tom Cruise").birthYear(1962).countryCode("US")
                    .biography("Megastar known for doing his own stunts.").photoUrl(BASE + "maf8PhSvDCdEwjEMbYfGpojR5RP.jpg").build());
            Person sigourneyWeaver = personRepo.save(Person.builder().name("Sigourney Weaver").birthYear(1949).countryCode("US")
                    .biography("Pioneer of strong female roles in sci-fi.").photoUrl(BASE + "wTSnfktNBLd6kwQxgvkqYw6vEon.jpg").build());
            Person jackNicholson = personRepo.save(Person.builder().name("Jack Nicholson").birthYear(1937).countryCode("US")
                    .biography("Three-time Oscar winner.").photoUrl(BASE + "hBHcQIEa6P48HQAlLZkh0eKSSkG.jpg").build());
            Person cateBlanchett = personRepo.save(Person.builder().name("Cate Blanchett").birthYear(1969).countryCode("AU")
                    .biography("Two-time Oscar winner known for range.").photoUrl(BASE + "9ZhDs8qPwIYLDQDk9YUCo6bs5Li.jpg").build());
            Person harrisonFord = personRepo.save(Person.builder().name("Harrison Ford").birthYear(1942).countryCode("US")
                    .biography("Star of Star Wars and Indiana Jones.").photoUrl(BASE + "zVnHagUvXkR2StdOtquEwsiwSVt.jpg").build());
            Person bruceWillis = personRepo.save(Person.builder().name("Bruce Willis").birthYear(1955).countryCode("US")
                    .biography("Action hero known for Die Hard.").photoUrl(BASE + "w3aXr1e7gQCn8MSp1vW4sXHn99P.jpg").build());
            Person elijahWood = personRepo.save(Person.builder().name("Elijah Wood").birthYear(1981).countryCode("US")
                    .biography("Known for the Lord of the Rings trilogy.").photoUrl(BASE + "ayARmqAe9Aab1zg6FjJG0u9MEBo.jpg").build());
            Person ianMcKellen = personRepo.save(Person.builder().name("Ian McKellen").birthYear(1939).countryCode("GB")
                    .biography("Acclaimed stage and screen actor.").photoUrl(BASE + "coWjgMEYJjk2OrNddlXCBm8EIr3.jpg").build());
            Person chadwickBoseman = personRepo.save(Person.builder().name("Chadwick Boseman").birthYear(1976).countryCode("US")
                    .biography("Star of Black Panther.").photoUrl(BASE + "1lz1wLOuPFSRIratMz0SxD3tkJ.jpg").build());
            Person timotheeChalamet = personRepo.save(Person.builder().name("Timothée Chalamet").birthYear(1995).countryCode("US")
                    .biography("Rising star of Dune and Call Me by Your Name.").photoUrl(BASE + "dFxpwRpmzpVfP1zjluH68DeQhyj.jpg").build());
            Person scarlettJohansson = personRepo.save(Person.builder().name("Scarlett Johansson").birthYear(1984).countryCode("US")
                    .biography("Star of the Avengers franchise.").photoUrl(BASE + "mjReG6rR7NPMEIWb1T4YWtV11ty.jpg").build());
            Person danielCraig = personRepo.save(Person.builder().name("Daniel Craig").birthYear(1968).countryCode("GB")
                    .biography("Known for his role as James Bond.").photoUrl(BASE + "iFerDZUmC5Fu26i4qI8xnUVEHc7.jpg").build());
            Person jessicaChastain = personRepo.save(Person.builder().name("Jessica Chastain").birthYear(1977).countryCode("US")
                    .biography("Oscar-winning actress known for dramatic roles.").photoUrl(BASE + "lodMzLKSdrPcBry6TdoDsMN3Vge.jpg").build());
            Person kevinSpacey = personRepo.save(Person.builder().name("Kevin Spacey").birthYear(1959).countryCode("US")
                    .biography("Two-time Oscar winner.").photoUrl(BASE + "nPrUZDEbGQe6jwpVbHKJCXsMd7r.jpg").build());
            Person clintEastwood = personRepo.save(Person.builder().name("Clint Eastwood").birthYear(1930).countryCode("US")
                    .biography("Iconic actor and director known for Westerns and thrillers.").photoUrl(BASE + "8TwdCfeOZH7ucRlfLZ6wObxa7cO.jpg").build());
            Person ewanMcGregor = personRepo.save(Person.builder().name("Ewan McGregor").birthYear(1971).countryCode("GB")
                    .biography("Known for the Star Wars prequel trilogy and Trainspotting.").photoUrl(BASE + "tw6lVBh0DvAUkCd1jsU98yD1usk.jpg").build());
            Person daisyRidley = personRepo.save(Person.builder().name("Daisy Ridley").birthYear(1992).countryCode("GB")
                    .biography("Star of the Star Wars sequel trilogy as Rey.").photoUrl(BASE + "iVboQmgPC3tYFjezBjrVECJRS8n.jpg").build());
            Person lindaHamilton = personRepo.save(Person.builder().name("Linda Hamilton").birthYear(1956).countryCode("US")
                    .biography("Known for her role as Sarah Connor in the Terminator franchise.").photoUrl(BASE + "7FNn9Z5xkRS9EFbGL2tpmpph9xV.jpg").build());
            Person timRobbins = personRepo.save(Person.builder().name("Tim Robbins").birthYear(1958).countryCode("US")
                    .biography("Known for The Shawshank Redemption and Mystic River.").photoUrl(BASE + "q9Q3c7HsZEEqrwNIY9yDgsJ74uE.jpg").build());
            Person henryFonda = personRepo.save(Person.builder().name("Henry Fonda").birthYear(1905).countryCode("US")
                    .biography("Legendary actor known for 12 Angry Men and The Grapes of Wrath.").photoUrl(BASE + "1G8TxAQnndnY5CLqVApBQ8RUT4A.jpg").build());
            Person anthonyHopkins = personRepo.save(Person.builder().name("Anthony Hopkins").birthYear(1937).countryCode("GB")
                    .biography("Oscar winner known for The Silence of the Lambs.").photoUrl(BASE + "dYVQTK1dPrQl1mugeLEWSSmA6Im.jpg").build());
            Person jodieFoster = personRepo.save(Person.builder().name("Jodie Foster").birthYear(1962).countryCode("US")
                    .biography("Oscar-winning actress known for The Silence of the Lambs.").photoUrl(BASE + "v6ezjezzDo6xP2wlONO5ZzBciwl.jpg").build());
            Person emilyBlunt = personRepo.save(Person.builder().name("Emily Blunt").birthYear(1983).countryCode("GB")
                    .biography("Known for A Quiet Place and Edge of Tomorrow.").photoUrl(BASE + "5nCSG5TL1bP1geD8aaBfaLnLLCD.jpg").build());
            Person anthonyPerkins = personRepo.save(Person.builder().name("Anthony Perkins").birthYear(1932).countryCode("US")
                    .biography("Best known for his role as Norman Bates in Psycho.").photoUrl(BASE + "rrLTDDFo23kSdvj19qMaUEVI9BQ.jpg").build());
            Person janetLeigh = personRepo.save(Person.builder().name("Janet Leigh").birthYear(1927).countryCode("US")
                    .biography("Known for her iconic role in Psycho.").photoUrl(BASE + "fe7QwANelGt0M1PLKj9qTJF9FZu.jpg").build());
            Person humphreyBogart = personRepo.save(Person.builder().name("Humphrey Bogart").birthYear(1899).countryCode("US")
                    .biography("Classic Hollywood icon known for Casablanca and The Maltese Falcon.").photoUrl(BASE + "4pk2VbOb2td7iBZyir6Ji46HH4N.jpg").build());
            Person ingridBergman = personRepo.save(Person.builder().name("Ingrid Bergman").birthYear(1915).countryCode("SE")
                    .biography("Legendary actress known for Casablanca and Notorious.").photoUrl(BASE + "lzXRh16qe4HHeBN6tMyw0DHvaMn.jpg").build());
            Person jenniferAniston = personRepo.save(Person.builder().name("Jennifer Aniston").birthYear(1969).countryCode("US")
                    .biography("Known for playing Rachel Green in Friends.").photoUrl(BASE + "vq7KKJE4gsb8WQEUkvMB2zUcsOO.jpg").build());
            Person courteneyCox = personRepo.save(Person.builder().name("Courteney Cox").birthYear(1964).countryCode("US")
                    .biography("Known for playing Monica Geller in Friends.").photoUrl(BASE + "cSOORhCRPJiwKghozXVXrOBi3Tp.jpg").build());
            Person lisaKudrow = personRepo.save(Person.builder().name("Lisa Kudrow").birthYear(1963).countryCode("US")
                    .biography("Known for playing Phoebe Buffay in Friends.").photoUrl(BASE + "ziatnwJRiBJIcc8jlk6xoClhfOy.jpg").build());
            Person mattLeBlanc = personRepo.save(Person.builder().name("Matt LeBlanc").birthYear(1967).countryCode("US")
                    .biography("Known for playing Joey Tribbiani in Friends.").photoUrl(BASE + "4oGrLuAVBHPqRbbaQH6p85bEDwu.jpg").build());
            Person matthewPerry = personRepo.save(Person.builder().name("Matthew Perry").birthYear(1969).countryCode("CA")
                    .biography("Known for playing Chandler Bing in Friends.").photoUrl(BASE + "ecDzkLWPV1z0x31I1GTjNmLxAHk.jpg").build());
            Person davidSchwimmer = personRepo.save(Person.builder().name("David Schwimmer").birthYear(1966).countryCode("US")
                    .biography("Known for playing Ross Geller in Friends.").photoUrl(BASE + "cNwpRXSN5mxlT7Gee3JayYHae1b.jpg").build());
            Person jerrySeinfeld = personRepo.save(Person.builder().name("Jerry Seinfeld").birthYear(1954).countryCode("US")
                    .biography("Stand-up comedian and co-creator/star of Seinfeld.").photoUrl(BASE + "nZdVry7lnUkE24PnXakok9okvL4.jpg").build());
            Person juliaLouisDreyfus = personRepo.save(Person.builder().name("Julia Louis-Dreyfus").birthYear(1961).countryCode("US")
                    .biography("Emmy-winning actress known for Seinfeld and Veep.").photoUrl(BASE + "2QUEYVhrKbOkRKeFEUnc5sJby6a.jpg").build());
            Person jasonAlexander = personRepo.save(Person.builder().name("Jason Alexander").birthYear(1959).countryCode("US")
                    .biography("Known for playing George Costanza in Seinfeld.").photoUrl(BASE + "qRezZ2yhM2bmBERt7jVcxo8RVSA.jpg").build());
            Person michaelRichards = personRepo.save(Person.builder().name("Michael Richards").birthYear(1949).countryCode("US")
                    .biography("Known for playing Cosmo Kramer in Seinfeld.").photoUrl(BASE + "bAYwmBdPJAiFkossWq56pEDmUh4.jpg").build());
            Person emiliaClarke = personRepo.save(Person.builder().name("Emilia Clarke").birthYear(1986).countryCode("GB")
                    .biography("Known for playing Daenerys Targaryen in Game of Thrones.").photoUrl(BASE + "6Sjz9teWjrMY9lF2o9FCo4XmoRh.jpg").build());
            Person kitHarington = personRepo.save(Person.builder().name("Kit Harington").birthYear(1986).countryCode("GB")
                    .biography("Known for playing Jon Snow in Game of Thrones.").photoUrl(BASE + "iGXlJbExWwZmo9sUDsYuzf4Sv4y.jpg").build());
            Person peterDinklage = personRepo.save(Person.builder().name("Peter Dinklage").birthYear(1969).countryCode("US")
                    .biography("Emmy-winning actor known for Tyrion Lannister in Game of Thrones.").photoUrl(BASE + "9CAd7wr8QZyIN0E7nm8v1B6WkGn.jpg").build());
            Person markHamill = personRepo.save(Person.builder().name("Mark Hamill").birthYear(1951).countryCode("US")
                    .biography("Iconic as Luke Skywalker in the Star Wars franchise.").photoUrl(BASE + "zMQ93JTLW8KxusKhOlHFZhih3YQ.jpg").build());
            Person carrieFisher = personRepo.save(Person.builder().name("Carrie Fisher").birthYear(1956).countryCode("US")
                    .biography("Beloved as Princess Leia in the Star Wars franchise.").photoUrl(BASE + "of4yHmryKPy92eeskUQ7MRmjC3l.jpg").build());
            Person adamDriver = personRepo.save(Person.builder().name("Adam Driver").birthYear(1983).countryCode("US")
                    .biography("Known for his role as Kylo Ren in the Star Wars sequel trilogy.").photoUrl(BASE + "fsbGQ1eZFgdsG1XnKlhNSvHsiGo.jpg").build());
            Person carrieAnneMoss = personRepo.save(Person.builder().name("Carrie-Anne Moss").birthYear(1967).countryCode("CA")
                    .biography("Known for her role as Trinity in The Matrix franchise.").photoUrl(BASE + "9msSN9TnF6Ne5cyBwrFZjrjwYbR.jpg").build());
            Person zendaya = personRepo.save(Person.builder().name("Zendaya").birthYear(1996).countryCode("US")
                    .biography("Multi-talented actress known for Dune and Euphoria.").photoUrl(BASE + "3WdOloHpjtjL96uVOhFRRCcYSwq.jpg").build());
            Person tomHardy = personRepo.save(Person.builder().name("Tom Hardy").birthYear(1977).countryCode("GB")
                    .biography("Versatile actor known for intense and physical roles.").photoUrl(BASE + "d81K0RH8UX7tZj49tZaQhZ9ewH.jpg").build());
            Person rayLiotta = personRepo.save(Person.builder().name("Ray Liotta").birthYear(1954).countryCode("US")
                    .biography("Known for his breakthrough role in Goodfellas.").photoUrl(BASE + "rhaCUi04uEXDFvuPM5Drj1AprE6.jpg").build());
            Person alanRickman = personRepo.save(Person.builder().name("Alan Rickman").birthYear(1946).countryCode("GB")
                    .biography("Known for playing iconic villains and complex characters.").photoUrl(BASE + "bVZRMlpjTAO2pJK6v90buFgVbSW.jpg").build());
            Person anaDeArmas = personRepo.save(Person.builder().name("Ana de Armas").birthYear(1988).countryCode("CU")
                    .biography("Rising star known for Knives Out and No Time to Die.").photoUrl(BASE + "3vxvsmYLTf4jnr163SUlBIw51ee.jpg").build());
            Person robinWright = personRepo.save(Person.builder().name("Robin Wright").birthYear(1966).countryCode("US")
                    .biography("Known for Forrest Gump and House of Cards.").photoUrl(BASE + "1p2aSnSkYi0maqqdpzQ73KZSDPO.jpg").build());
            Person shelleyDuvall = personRepo.save(Person.builder().name("Shelley Duvall").birthYear(1949).countryCode("US")
                    .biography("Known for her iconic role in The Shining.").photoUrl(BASE + "gf44Hr3HJuWK7ZMHQKzDNBe0ylI.jpg").build());
            Person eliWallach = personRepo.save(Person.builder().name("Eli Wallach").birthYear(1915).countryCode("US")
                    .biography("Legendary character actor known for The Good, the Bad and the Ugly.").photoUrl(BASE + "egLe8r2PwbTx9ocwS1Zu2vsYC9v.jpg").build());
            Person leeJCobb = personRepo.save(Person.builder().name("Lee J. Cobb").birthYear(1911).countryCode("US")
                    .biography("Powerful character actor known for 12 Angry Men.").photoUrl(BASE + "yxMxBvM0PZwu7YXQamG0kFwt9DZ.jpg").build());
            Person milesTeller = personRepo.save(Person.builder().name("Miles Teller").birthYear(1987).countryCode("US")
                    .biography("Known for Whiplash and Top Gun: Maverick.").photoUrl(BASE + "aciu7YM8fD0BzrrA6cJ5wDKZIA6.jpg").build());
            Person claireDanes = personRepo.save(Person.builder().name("Claire Danes").birthYear(1979).countryCode("US")
                    .biography("Known for Homeland and Terminator 3.").photoUrl(BASE + "vTKKniIwbXWMmnuMmGKHYS41Vif.jpg").build());
            Person samWorthington = personRepo.save(Person.builder().name("Sam Worthington").birthYear(1976).countryCode("AU")
                    .biography("Known for Avatar and Terminator Salvation.").photoUrl(BASE + "mflBcox36s9ZPbsZPVOuhf6axaJ.jpg").build());
            Person andyGarcia = personRepo.save(Person.builder().name("Andy Garcia").birthYear(1956).countryCode("CU")
                    .biography("Known for The Godfather Part III and Ocean's Eleven.").photoUrl(BASE + "9EivXoBlczZcFBet96WOoFbDsfF.jpg").build());
            Person williamSadler = personRepo.save(Person.builder().name("William Sadler").birthYear(1950).countryCode("US")
                    .biography("Known for Die Hard 2 and The Shawshank Redemption.").photoUrl(BASE + "xC9sijoDnjS3oDZ5eszcGKHKAOp.jpg").build());
            Person justinLong = personRepo.save(Person.builder().name("Justin Long").birthYear(1978).countryCode("US")
                    .biography("Known for his role in Live Free or Die Hard.").photoUrl(BASE + "7TGXeHw4o86IBm6xknQotpludXK.jpg").build());
            Person jaiCourtney = personRepo.save(Person.builder().name("Jai Courtney").birthYear(1986).countryCode("AU")
                    .biography("Known for Die Hard and Terminator Genisys.").photoUrl(BASE + "6vEaNwbOKov6yzQx15CdtrqfK3L.jpg").build());
            Person tomSkerritt = personRepo.save(Person.builder().name("Tom Skerritt").birthYear(1933).countryCode("US")
                    .biography("Known for Alien and Top Gun.").photoUrl(BASE + "oWFCyBLm1lsbsbT5Nmx3SPMaqFZ.jpg").build());
            Person arnoldSchwarzenegger = personRepo.save(Person.builder().name("Arnold Schwarzenegger").birthYear(1947).countryCode("AT")
                    .biography("Action legend and former California governor.").photoUrl(BASE + "dgCABuZp2HBehCT84O4WBp7KIoe.jpg").build());
            Person aneurinBarnard = personRepo.save(Person.builder().name("Aneurin Barnard").birthYear(1987).countryCode("GB")
                    .biography("Welsh actor known for Dunkirk and War & Peace.").photoUrl(BASE + "aiuFLvyaxmuglNi3nfQ3oL9qW97.jpg").build());
            Person haroldRamis = personRepo.save(Person.builder().name("Harold Ramis").birthYear(1944).countryCode("US")
                    .biography("Director of Groundhog Day and Caddyshack.").photoUrl(BASE + "kmTMTxthkDAsygk3Am4IUSVYYRi.jpg").build());
            Person wesAnderson = personRepo.save(Person.builder().name("Wes Anderson").birthYear(1969).countryCode("US")
                    .biography("Director known for a meticulously symmetrical visual style.").photoUrl(BASE + "s03CeUeC5yAXyB1acqP0zGNo2SC.jpg").build());
            Person billyWilder = personRepo.save(Person.builder().name("Billy Wilder").birthYear(1906).countryCode("AT")
                    .biography("Director of Some Like It Hot and Sunset Boulevard.").photoUrl(BASE + "nWV9BDDCbRegP7etiPjl3vYnJEq.jpg").build());
            Person damienChazelle = personRepo.save(Person.builder().name("Damien Chazelle").birthYear(1985).countryCode("US")
                    .biography("Director of La La Land and Whiplash.").photoUrl(BASE + "14kRZ3XxNMyBv717YQSXr3wCucy.jpg").build());
            Person michelGondry = personRepo.save(Person.builder().name("Michel Gondry").birthYear(1963).countryCode("FR")
                    .biography("Director known for a handmade, surreal visual style.").photoUrl(BASE + "4U9DhwQtrs2UUFPya2ixaYh091i.jpg").build());
            Person robReiner = personRepo.save(Person.builder().name("Rob Reiner").birthYear(1947).countryCode("US")
                    .biography("Director of When Harry Met Sally and Stand by Me.").photoUrl(BASE + "rcmPU3YlhHQVzZlV197qhmRsgEL.jpg").build());
            Person jordanPeele = personRepo.save(Person.builder().name("Jordan Peele").birthYear(1979).countryCode("US")
                    .biography("Director of Get Out and Us.").photoUrl(BASE + "kFUKn5g3ebpyZ3CSZZZo2HFWRNQ.jpg").build());
            Person ariAster = personRepo.save(Person.builder().name("Ari Aster").birthYear(1986).countryCode("US")
                    .biography("Director of Hereditary and Midsommar.").photoUrl(BASE + "45lOHyHwdMgyKm6u3jwLtyfwOjc.jpg").build());
            Person billMurray = personRepo.save(Person.builder().name("Bill Murray").birthYear(1950).countryCode("US")
                    .biography("Deadpan comic actor known for Groundhog Day and Ghostbusters.").photoUrl(BASE + "nnCsJc9x3ZiG3AFyiyc3FPehppy.jpg").build());
            Person andieMacDowell = personRepo.save(Person.builder().name("Andie MacDowell").birthYear(1958).countryCode("US")
                    .biography("Known for Groundhog Day and Four Weddings and a Funeral.").photoUrl(BASE + "akeheO4i3cR1HpSU8yu9HrLcsKm.jpg").build());
            Person ralphFiennes = personRepo.save(Person.builder().name("Ralph Fiennes").birthYear(1962).countryCode("GB")
                    .biography("Known for The Grand Budapest Hotel and Schindler's List.").photoUrl(BASE + "tJr9GcmGNHhLVVEH3i7QYbj6hBi.jpg").build());
            Person tonyRevolori = personRepo.save(Person.builder().name("Tony Revolori").birthYear(1996).countryCode("US")
                    .biography("Known for The Grand Budapest Hotel.").photoUrl(BASE + "tSF6XmXDikrKZbFUeoDnafXxKjT.jpg").build());
            Person marilynMonroe = personRepo.save(Person.builder().name("Marilyn Monroe").birthYear(1926).countryCode("US")
                    .biography("Screen icon known for Some Like It Hot.").photoUrl(BASE + "vcp6rHL2sZWdlJQDEWsEe0jAAe1.jpg").build());
            Person tonyCurtis = personRepo.save(Person.builder().name("Tony Curtis").birthYear(1925).countryCode("US")
                    .biography("Known for Some Like It Hot and Spartacus.").photoUrl(BASE + "6Pj1XkYdI5Wc40Jl0UxJk8PPwlY.jpg").build());
            Person jackLemmon = personRepo.save(Person.builder().name("Jack Lemmon").birthYear(1925).countryCode("US")
                    .biography("Known for Some Like It Hot and The Apartment.").photoUrl(BASE + "7jWVWLr1VJqQrN3d4YJaATCIHRw.jpg").build());
            Person ryanGosling = personRepo.save(Person.builder().name("Ryan Gosling").birthYear(1980).countryCode("CA")
                    .biography("Known for La La Land and Blade Runner 2049.").photoUrl(BASE + "lyUyVARQKhGxaxy0FbPJCQRpiaW.jpg").build());
            Person emmaStone = personRepo.save(Person.builder().name("Emma Stone").birthYear(1988).countryCode("US")
                    .biography("Known for La La Land and Poor Things.").photoUrl(BASE + "t7EYLBMWQiIDtCoOYZjvqXV84S5.jpg").build());
            Person jimCarrey = personRepo.save(Person.builder().name("Jim Carrey").birthYear(1962).countryCode("CA")
                    .biography("Known for Eternal Sunshine of the Spotless Mind and The Truman Show.").photoUrl(BASE + "y3U9QfPN6sJaGl6l68xjwWj28ig.jpg").build());
            Person kateWinslet = personRepo.save(Person.builder().name("Kate Winslet").birthYear(1975).countryCode("GB")
                    .biography("Known for Eternal Sunshine of the Spotless Mind and Titanic.").photoUrl(BASE + "6qNnMsKtKz9si5rabpUEG85UfHp.jpg").build());
            Person billyCrystal = personRepo.save(Person.builder().name("Billy Crystal").birthYear(1948).countryCode("US")
                    .biography("Known for When Harry Met Sally.").photoUrl(BASE + "jfcCceTnP7rrHz1pSjCxiqRosw3.jpg").build());
            Person megRyan = personRepo.save(Person.builder().name("Meg Ryan").birthYear(1961).countryCode("US")
                    .biography("Known for When Harry Met Sally and Sleepless in Seattle.").photoUrl(BASE + "pxfTSfJ4yJWq52uomSWKzigngOa.jpg").build());
            Person danielKaluuya = personRepo.save(Person.builder().name("Daniel Kaluuya").birthYear(1989).countryCode("GB")
                    .biography("Known for Get Out and Black Panther.").photoUrl(BASE + "jj2kZqJobjom36wlhlYhc38nTwN.jpg").build());
            Person allisonWilliams = personRepo.save(Person.builder().name("Allison Williams").birthYear(1988).countryCode("US")
                    .biography("Known for Get Out and Girls.").photoUrl(BASE + "5Jy9HELKS1OYg7moRl8870OSfJq.jpg").build());
            Person toniCollette = personRepo.save(Person.builder().name("Toni Collette").birthYear(1972).countryCode("AU")
                    .biography("Known for Hereditary and The Sixth Sense.").photoUrl(BASE + "lzXRh16qe4HHeBN6tMyw0DHvaMn.jpg").build());
            Person alexWolff = personRepo.save(Person.builder().name("Alex Wolff").birthYear(1997).countryCode("US")
                    .biography("Known for Hereditary.").photoUrl(BASE + "7PMu5zFOEH7PqFDzrKThgKD4Ndf.jpg").build());
            Person robertHays = personRepo.save(Person.builder().name("Robert Hays").birthYear(1947).countryCode("US")
                    .biography("Known for starring in Airplane! as the accident-prone pilot Ted Striker.").photoUrl(BASE + "pqu0hSAdHGItjP0iqkbTg4Ocv9w.jpg").build());
            Person julieHagerty = personRepo.save(Person.builder().name("Julie Hagerty").birthYear(1955).countryCode("US")
                    .biography("Known for Airplane! and Lost in America.").photoUrl(BASE + "bQZ0bcFNyE4a3GDNfYMmfO7eWMl.jpg").build());
            Person leslieNielsen = personRepo.save(Person.builder().name("Leslie Nielsen").birthYear(1926).countryCode("CA")
                    .biography("Deadpan comic actor known for Airplane! and the Naked Gun films.").photoUrl(BASE + "rMz3ZUmFE7lQCP51Ql7IcMHJIUZ.jpg").build());
            Person kareemAbdulJabbar = personRepo.save(Person.builder().name("Kareem Abdul-Jabbar").birthYear(1947).countryCode("US")
                    .biography("NBA Hall of Famer known for a memorable cameo in Airplane!").photoUrl(BASE + "aMbhI6nWjU3MkUhw9IIKnJz7UiX.jpg").build());
            Person jerryZucker = personRepo.save(Person.builder().name("Jerry Zucker").birthYear(1950).countryCode("US")
                    .biography("Co-director of Airplane! and Ghost.").photoUrl(BASE + "iSzdnge16GBKLkBoTM4mqUIWIxi.jpg").build());
            Person davidZucker = personRepo.save(Person.builder().name("David Zucker").birthYear(1947).countryCode("US")
                    .biography("Co-director of Airplane! and The Naked Gun.").photoUrl(BASE + "13iJccfZeC0X9WZgJnlI7ZphL7l.jpg").build());
            Person jimAbrahams = personRepo.save(Person.builder().name("Jim Abrahams").birthYear(1944).countryCode("US")
                    .biography("Co-director of Airplane! and the Naked Gun trilogy.").photoUrl(BASE + "22B24mvWEkorhoxEOPCxNcYdcTX.jpg").build());
            Person jeffBridges = personRepo.save(Person.builder().name("Jeff Bridges").birthYear(1949).countryCode("US")
                    .biography("Known for The Big Lebowski and Crazy Heart.").photoUrl(BASE + "xms1RAY6q7Lzp7wNeRCB0kzhucn.jpg").build());
            Person johnGoodman = personRepo.save(Person.builder().name("John Goodman").birthYear(1952).countryCode("US")
                    .biography("Known for The Big Lebowski and Roseanne.").photoUrl(BASE + "yyYqoyKHO7hE1zpgEV2XlqYWcNV.jpg").build());
            Person julianneMoore = personRepo.save(Person.builder().name("Julianne Moore").birthYear(1960).countryCode("US")
                    .biography("Known for The Big Lebowski and Still Alice.").photoUrl(BASE + "3YF19rWusxWfEI59ZM33dFhasRq.jpg").build());
            Person steveBuscemi = personRepo.save(Person.builder().name("Steve Buscemi").birthYear(1957).countryCode("US")
                    .biography("Known for The Big Lebowski and Reservoir Dogs.").photoUrl(BASE + "n0pZumkrcZrAPMoPq684RhYnjPV.jpg").build());
            Person joelCoen = personRepo.save(Person.builder().name("Joel Coen").birthYear(1954).countryCode("US")
                    .biography("Co-director of The Big Lebowski and Fargo.").photoUrl(BASE + "rgVaJNkZCgMarUcZuUAsVfXMWk3.jpg").build());
            Person ethanCoen = personRepo.save(Person.builder().name("Ethan Coen").birthYear(1957).countryCode("US")
                    .biography("Co-director of The Big Lebowski and No Country for Old Men.").photoUrl(BASE + "lbL8LEcvuxNrzda37g3mysOS2qS.jpg").build());
            Person matthewBroderick = personRepo.save(Person.builder().name("Matthew Broderick").birthYear(1962).countryCode("US")
                    .biography("Known for playing the title role in Ferris Bueller's Day Off.").photoUrl(BASE + "papqFgpyroZJEqd7WvuNGN8ti2k.jpg").build());
            Person alanRuck = personRepo.save(Person.builder().name("Alan Ruck").birthYear(1956).countryCode("US")
                    .biography("Known for Ferris Bueller's Day Off and Succession.").photoUrl(BASE + "hj7CuWinT12hMKjRhSO4XEMVq7w.jpg").build());
            Person miaSara = personRepo.save(Person.builder().name("Mia Sara").birthYear(1967).countryCode("US")
                    .biography("Known for Ferris Bueller's Day Off.").photoUrl(BASE + "gF1hYh0Rw5qCryiwGAWDdmiLufc.jpg").build());
            Person jeffreyJones = personRepo.save(Person.builder().name("Jeffrey Jones").birthYear(1946).countryCode("US")
                    .biography("Known for playing the school principal in Ferris Bueller's Day Off.").photoUrl(BASE + "2vlQDwmMlAAEKqdFIey0liR0T1E.jpg").build());
            Person johnHughes = personRepo.save(Person.builder().name("John Hughes").birthYear(1950).countryCode("US")
                    .biography("Director of Ferris Bueller's Day Off and Home Alone.").photoUrl(BASE + "7QBh9D3Qjf667Q549VeJAAV7O38.jpg").build());
            Person bradleyCooper = personRepo.save(Person.builder().name("Bradley Cooper").birthYear(1975).countryCode("US")
                    .biography("Known for The Hangover and A Star Is Born.").photoUrl(BASE + "pLD2XvxqHueLWOuqXoFngJU3A5H.jpg").build());
            Person edHelms = personRepo.save(Person.builder().name("Ed Helms").birthYear(1974).countryCode("US")
                    .biography("Known for The Hangover and The Office.").photoUrl(BASE + "gPZ8tZaNQGAc3KZRIPp9rgGbEnN.jpg").build());
            Person zachGalifianakis = personRepo.save(Person.builder().name("Zach Galifianakis").birthYear(1969).countryCode("US")
                    .biography("Known for The Hangover and Baskets.").photoUrl(BASE + "ncBSLoNUufKpg7vzx1gCtjhst8i.jpg").build());
            Person justinBartha = personRepo.save(Person.builder().name("Justin Bartha").birthYear(1978).countryCode("US")
                    .biography("Known for The Hangover and National Treasure.").photoUrl(BASE + "AalKjN8nR8RZ8g6ZdWV2smnQq4d.jpg").build());
            Person toddPhillips = personRepo.save(Person.builder().name("Todd Phillips").birthYear(1970).countryCode("US")
                    .biography("Director of The Hangover and Joker.").photoUrl(BASE + "A6FPht87DiqXzp456WjakLi2AtP.jpg").build());

            // ── Movies ────────────────────────────────────────────────────

            // DRAMA
            Movie shawshank = saveMovie(Movie.builder().title("The Shawshank Redemption").releaseYear(1994).genre(Genre.DRAMA).rating(9.3).runtime(142)
                    .plot("Imprisoned in the 1940s for the double murder of his wife and her lover, upstanding banker Andy Dufresne begins a new life at the Shawshank prison, where he puts his accounting skills to work for an amoral warden. During his long stretch in prison, Dufresne comes to be admired by the other inmates, including an older prisoner named Red, for his integrity and unquenchable sense of hope.")
                    .posterUrl(BASE + "9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg").tmdbId(278).build(),
                List.of(frankDarabont),
                List.of(new CastEntry(timRobbins, "Andy Dufresne"), new CastEntry(morganFreeman, "Ellis 'Red' Redding")));
            Movie godfather = saveMovie(Movie.builder().title("The Godfather").releaseYear(1972).genre(Genre.CRIME).rating(9.2).runtime(175)
                    .plot("Spanning the years 1945 to 1955, a chronicle of the fictional Italian-American Corleone crime family. When organized crime family patriarch, Vito Corleone barely survives an attempt on his life, his youngest son, Michael steps in to take care of the would-be killers, launching a campaign of bloody revenge.")
                    .posterUrl(BASE + "3bhkrj58Vtu7enYsRolD1fZdja1.jpg").tmdbId(238).build(),
                List.of(francisFordCoppola),
                List.of(new CastEntry(marlonBrando, "Vito Corleone"), new CastEntry(alPacino, "Michael Corleone")));
            saveMovie(Movie.builder().title("The Godfather Part II").releaseYear(1974).genre(Genre.CRIME).rating(9.0).runtime(202)
                    .plot("In the continuing saga of the Corleone crime family, a young Vito Corleone grows up in Sicily and in 1910s New York. In the 1950s, Michael Corleone attempts to expand the family business into Las Vegas, Hollywood and Cuba.")
                    .posterUrl(BASE + "hek3koDUyRQk7FIhPXsa6mT2Zc3.jpg").tmdbId(240).build(),
                List.of(francisFordCoppola),
                List.of(new CastEntry(alPacino, "Michael Corleone"), new CastEntry(robertDeNiro, "Young Vito Corleone")));
            saveMovie(Movie.builder().title("The Godfather Part III").releaseYear(1990).genre(Genre.CRIME).rating(7.6).runtime(162)
                    .plot("In the midst of trying to legitimize his business dealings in 1979 New York and Italy, aging mafia don, Michael Corleone seeks forgiveness for his sins while taking a young protege under his wing.")
                    .posterUrl(BASE + "lm3pQ2QoQ16pextRsmnUbG2onES.jpg").tmdbId(242).build(),
                List.of(francisFordCoppola),
                List.of(new CastEntry(alPacino, "Michael Corleone"), new CastEntry(andyGarcia, "Vincent Corleone")));
            saveMovie(Movie.builder().title("Forrest Gump").releaseYear(1994).genre(Genre.DRAMA).rating(8.8).runtime(142)
                    .plot("A man with a low IQ has accomplished great things in his life and been present during significant historic events, in each case far exceeding what anyone imagined he could do. But despite all he has achieved, his one true love eludes him.")
                    .posterUrl(BASE + "saHP97rTPS5eLmrLQEcANmKrsFl.jpg").tmdbId(13).build(),
                List.of(robertZemeckis),
                List.of(new CastEntry(tomHanks, "Forrest Gump"), new CastEntry(robinWright, "Jenny Curran")));
            saveMovie(Movie.builder().title("12 Angry Men").releaseYear(1957).genre(Genre.DRAMA).rating(9.0).runtime(96)
                    .plot("The defense and the prosecution have rested and the jury is filing into the jury room to decide if a young Spanish-American is guilty or innocent of murdering his father. What begins as an open and shut case soon becomes a mini-drama of each of the jurors' prejudices and preconceptions about the trial, the accused, and each other.")
                    .posterUrl(BASE + "2QXLVh32JKaWTjFJU3n8aIxRK9P.jpg").tmdbId(389).build(),
                List.of(sidneyLumet),
                List.of(new CastEntry(henryFonda, "Juror 8"), new CastEntry(leeJCobb, "Juror 3")));

            // ACTION
            saveMovie(Movie.builder().title("Die Hard").releaseYear(1988).genre(Genre.ACTION).rating(8.2).runtime(132)
                    .plot("High above the city of L.A. a team of terrorists has seized a building, taken hostages, and declared war. One man manages to escape... An off-duty cop hiding somewhere inside. He's alone, tired... and the only chance anyone has got.")
                    .posterUrl(BASE + "7Bjd8kfmDSOzpmhySpEhkUyK2oH.jpg").tmdbId(562).build(),
                List.of(johnMcTiernan),
                List.of(new CastEntry(bruceWillis, "John McClane"), new CastEntry(alanRickman, "Hans Gruber")));
            saveMovie(Movie.builder().title("Die Hard 2").releaseYear(1990).genre(Genre.ACTION).rating(7.1).runtime(124)
                    .plot("One year after his heroics in Los Angeles, John McClane is an off-duty cop who is the wrong guy in the wrong place at the wrong time. On a snowy Christmas Eve, as he waits for his wife's plane to land at Washington Dulles International Airport, terrorists take over the air traffic control system in a plot to free a South American army general and drug smuggler being flown into the US to face drug charges. It's now up to McClane to take on the terrorists, while coping with an inept airport police chief, an uncooperative anti-terrorist squad, and the life of his wife and everyone else trapped in planes circling overhead.")
                    .posterUrl(BASE + "ybki0UWO3OPhaM6MSniuKC7sy1R.jpg").tmdbId(1573).build(),
                List.of(rennyHarlin),
                List.of(new CastEntry(bruceWillis, "John McClane"), new CastEntry(williamSadler, "Col. Stuart")));
            saveMovie(Movie.builder().title("Die Hard with a Vengeance").releaseYear(1995).genre(Genre.ACTION).rating(7.7).runtime(128)
                    .plot("New York detective John McClane is back and kicking bad-guy butt in the third installment of this action-packed series, which finds him teaming with civilian Zeus Carver to prevent the loss of innocent lives. McClane thought he'd seen it all, until a genius named Simon engages both McClane, his new \"partner\", and his beloved city in a deadly game that demands their concentration.")
                    .posterUrl(BASE + "buqmCdFQEWwEpL3agGgg2GVjN2d.jpg").tmdbId(1572).build(),
                List.of(johnMcTiernan),
                List.of(new CastEntry(bruceWillis, "John McClane"), new CastEntry(samuelLJackson, "Zeus Carver")));
            saveMovie(Movie.builder().title("Live Free or Die Hard").releaseYear(2007).genre(Genre.ACTION).rating(7.2).runtime(128)
                    .plot("John McClane is back and badder than ever, and this time he calls on the services of a young hacker in his bid to stop a ring of Internet terrorists intent on taking control of America's computer infrastructure.")
                    .posterUrl(BASE + "31TT47YjBl7a7uvJ3ff1nrirXhP.jpg").tmdbId(1571).build(),
                List.of(lenWiseman),
                List.of(new CastEntry(bruceWillis, "John McClane"), new CastEntry(justinLong, "Matt Farrell")));
            saveMovie(Movie.builder().title("A Good Day to Die Hard").releaseYear(2013).genre(Genre.ACTION).rating(5.3).runtime(97)
                    .plot("Iconoclastic, take-no-prisoners cop John McClane, finds himself for the first time on foreign soil after traveling to Moscow to help his wayward son Jack - unaware that Jack is really a highly-trained CIA operative out to stop a nuclear weapons heist. With the Russian underworld in pursuit, and battling a countdown to war, the two McClanes discover that their opposing methods make them unstoppable heroes.")
                    .posterUrl(BASE + "qJ0csDXAVFMsNn0cRcjy6W6PxAK.jpg").tmdbId(47964).build(),
                List.of(johnMoore),
                List.of(new CastEntry(bruceWillis, "John McClane"), new CastEntry(jaiCourtney, "Jack McClane")));
            saveMovie(Movie.builder().title("Top Gun: Maverick").releaseYear(2022).genre(Genre.ACTION).rating(8.3).runtime(130)
                    .plot("After more than thirty years of service as one of the Navy’s top aviators, and dodging the advancement in rank that would ground him, Pete “Maverick” Mitchell finds himself training a detachment of TOP GUN graduates for a specialized mission the likes of which no living pilot has ever seen.")
                    .posterUrl(BASE + "62HCnUTziyWcpDaBO2i1DX17ljH.jpg").tmdbId(361743).build(),
                List.of(josephKosinski),
                List.of(new CastEntry(tomCruise, "Pete 'Maverick' Mitchell"), new CastEntry(milesTeller, "Bradley 'Rooster' Bradshaw")));
            saveMovie(Movie.builder().title("Avengers: Endgame").releaseYear(2019).genre(Genre.ACTION).rating(8.4).runtime(181)
                    .plot("After the devastating events of Avengers: Infinity War, the universe is in ruins due to the efforts of the Mad Titan, Thanos. With the help of remaining allies, the Avengers must assemble once more in order to undo Thanos' actions and restore order to the universe once and for all, no matter what consequences may be in store.")
                    .posterUrl(BASE + "ulzhLuWrPK07P1YkdWQLZnQh1JL.jpg").tmdbId(299534).build(),
                List.of(anthonyRusso, joeRusso),
                List.of(new CastEntry(scarlettJohansson, "Natasha Romanoff"), new CastEntry(chadwickBoseman, "T'Challa")));

            // COMEDY
            Movie groundhogDay = saveMovie(Movie.builder().title("Groundhog Day").releaseYear(1993).genre(Genre.COMEDY).rating(7.6).runtime(101)
                    .plot("A cynical TV weatherman, along with his idealistic producer and his sardonic cameraman, is sent to report on Groundhog Day in the small town of Punxsutawney, where he finds himself repeating the same day over and over.")
                    .posterUrl(BASE + "gCgt1WARPZaXnq523ySQEUKinCs.jpg").tmdbId(137).build(),
                List.of(haroldRamis),
                List.of(new CastEntry(billMurray, "Phil Connors"), new CastEntry(andieMacDowell, "Rita Hanson")));
            saveMovie(Movie.builder().title("The Grand Budapest Hotel").releaseYear(2014).genre(Genre.COMEDY).rating(8.1).runtime(99)
                    .plot("The Grand Budapest Hotel tells of a legendary concierge at a famous European hotel between the wars and his friendship with a young employee who becomes his trusted protégé. The story involves the theft and recovery of a priceless Renaissance painting, the battle for an enormous family fortune and the slow and then sudden upheavals that transformed Europe during the first half of the 20th century.")
                    .posterUrl(BASE + "eWdyYQreja6JGCzqHWXpWHDrrPo.jpg").tmdbId(120467).build(),
                List.of(wesAnderson),
                List.of(new CastEntry(ralphFiennes, "M. Gustave"), new CastEntry(tonyRevolori, "Zero Moustafa")));
            saveMovie(Movie.builder().title("Some Like It Hot").releaseYear(1959).genre(Genre.COMEDY).rating(8.2).runtime(121)
                    .plot("In Prohibition-era Chicago, musicians Joe and Jerry witness a mob hit, and flee the state in an all-female band disguised as Josephine and Daphne, but further complications set in.")
                    .posterUrl(BASE + "hVIKyTK13AvOGv7ICmJjK44DTzp.jpg").tmdbId(239).build(),
                List.of(billyWilder),
                List.of(new CastEntry(marilynMonroe, "Sugar Kane Kowalczyk"), new CastEntry(tonyCurtis, "Joe"), new CastEntry(jackLemmon, "Jerry")));
            saveMovie(Movie.builder().title("Airplane!").releaseYear(1980).genre(Genre.COMEDY).rating(7.3).runtime(88)
                    .plot("An ex-fighter pilot forced to take over the controls of an airliner when the flight crew succumbs to food poisoning.")
                    .posterUrl(BASE + "7Q3efxd3AF1vQjlSxnlerSA7RzN.jpg").tmdbId(813).build(),
                List.of(jerryZucker, davidZucker, jimAbrahams),
                List.of(new CastEntry(robertHays, "Ted Striker"), new CastEntry(julieHagerty, "Elaine Dickinson"), new CastEntry(leslieNielsen, "Dr. Rumack"), new CastEntry(kareemAbdulJabbar, "Roger Murdock")));
            saveMovie(Movie.builder().title("The Big Lebowski").releaseYear(1998).genre(Genre.COMEDY).rating(7.8).runtime(117)
                    .plot("Jeffrey 'The Dude' Lebowski, a Los Angeles slacker who only wants to bowl and drink White Russians, is mistaken for another Jeffrey Lebowski, a wheelchair-bound millionaire, and finds himself dragged into a strange series of events involving nihilists, adult film producers, ferrets, errant toes, and large sums of money.")
                    .posterUrl(BASE + "3bv6WAp6BSxxYvB5ozKFUYuRA8C.jpg").tmdbId(115).build(),
                List.of(joelCoen, ethanCoen),
                List.of(new CastEntry(jeffBridges, "The Dude"), new CastEntry(johnGoodman, "Walter Sobchak"), new CastEntry(julianneMoore, "Maude Lebowski"), new CastEntry(steveBuscemi, "Donny")));
            saveMovie(Movie.builder().title("Ferris Bueller's Day Off").releaseYear(1986).genre(Genre.COMEDY).rating(7.6).runtime(103)
                    .plot("After high school slacker Ferris Bueller successfully fakes an illness in order to skip school for the day, he goes on a series of adventures throughout Chicago with his girlfriend Sloane and best friend Cameron, all the while trying to outwit his wily school principal and fed-up sister.")
                    .posterUrl(BASE + "9LTQNCvoLsKXP0LtaKAaYVtRaQL.jpg").tmdbId(9377).build(),
                List.of(johnHughes),
                List.of(new CastEntry(matthewBroderick, "Ferris Bueller"), new CastEntry(alanRuck, "Cameron Frye"), new CastEntry(miaSara, "Sloane Peterson"), new CastEntry(jeffreyJones, "Ed Rooney")));
            saveMovie(Movie.builder().title("The Hangover").releaseYear(2009).genre(Genre.COMEDY).rating(7.3).runtime(100)
                    .plot("When three friends finally come to after a raucous night of bachelor-party revelry, they find a baby in the closet and a tiger in the bathroom. But they can't seem to locate their best friend, Doug, who's supposed to be tying the knot. Launching a frantic search for Doug, the trio perseveres through a nasty hangover to try to make it to the church on time.")
                    .posterUrl(BASE + "A0uS9rHR56FeBtpjVki16M5xxSW.jpg").tmdbId(18785).build(),
                List.of(toddPhillips),
                List.of(new CastEntry(bradleyCooper, "Phil Wenneck"), new CastEntry(edHelms, "Stu Price"), new CastEntry(zachGalifianakis, "Alan Garner"), new CastEntry(justinBartha, "Doug Billings")));

            // COMEDY (2016 and later, added so the "comedy under two hours" free-text example returns results)
            Person shaneBlack = personRepo.save(Person.builder().name("Shane Black").birthYear(1961).countryCode("US")
                    .biography("Writer-director known for irreverent buddy-comedy dialogue.").photoUrl(BASE + "fafBg8LjtQqsXyFg8ZgW7DHQXKt.jpg").build());
            Person russellCrowe = personRepo.save(Person.builder().name("Russell Crowe").birthYear(1964).countryCode("NZ")
                    .biography("Oscar-winning actor known for commanding dramatic roles.").photoUrl(BASE + "uxiXuVH4vNWrKlJMVVPG1sxAJFe.jpg").build());
            saveMovie(Movie.builder().title("The Nice Guys").releaseYear(2016).genre(Genre.COMEDY).rating(7.1).runtime(116)
                    .plot("A private eye investigates the apparent suicide of a fading porn star in 1970s Los Angeles and uncovers a conspiracy.")
                    .posterUrl(BASE + "clq4So9spa9cXk3MZy2iMdqkxP2.jpg").tmdbId(290250).build(),
                List.of(shaneBlack),
                List.of(new CastEntry(russellCrowe, "Jackson Healy"), new CastEntry(ryanGosling, "Holland March")));
            Person armandoIannucci = personRepo.save(Person.builder().name("Armando Iannucci").birthYear(1963).countryCode("GB")
                    .biography("Satirist known for razor-sharp political comedy.").photoUrl(BASE + "jFFx3mmVFA2ApjcVAhVVznjjrLI.jpg").build());
            Person simonRussellBeale = personRepo.save(Person.builder().name("Simon Russell Beale").birthYear(1961).countryCode("MY")
                    .biography("Acclaimed stage and screen actor known for classical roles.").photoUrl(BASE + "uw0ngaqgT8qXwJqVr353cx1mEdB.jpg").build());
            saveMovie(Movie.builder().title("The Death of Stalin").releaseYear(2017).genre(Genre.COMEDY).rating(7.0).runtime(107)
                    .plot("When dictator Joseph Stalin dies, his parasitic cronies square off in a frantic power struggle to become the next Soviet leader. As they bumble, brawl and back-stab their way to the top, the question remains: just who is running the government?")
                    .posterUrl(BASE + "AqH7q89NxGRDAyRWKqsL3OBtYfV.jpg").tmdbId(402897).build(),
                List.of(armandoIannucci),
                List.of(new CastEntry(steveBuscemi, "Nikita Khrushchev"), new CastEntry(simonRussellBeale, "Lavrenti Beria")));
            Person bryanCranston = personRepo.save(Person.builder().name("Bryan Cranston").birthYear(1956).countryCode("US")
                    .biography("Emmy-winning actor known for Breaking Bad.").photoUrl(BASE + "npIIZJGSrcJIJ6yHdmbqO6Jzo5I.jpg").build());
            Person edwardNorton = personRepo.save(Person.builder().name("Edward Norton").birthYear(1969).countryCode("US")
                    .biography("Versatile actor known for intense character studies.").photoUrl(BASE + "8nytsqL59SFJTVYVrN72k6qkGgJ.jpg").build());
            saveMovie(Movie.builder().title("Isle of Dogs").releaseYear(2018).genre(Genre.COMEDY).rating(7.8).runtime(101)
                    .plot("In the future, an outbreak of canine flu leads the mayor of a Japanese city to banish all dogs to an island used as a garbage dump. The outcasts must soon embark on an epic journey when a 12-year-old boy arrives on the island to find his beloved pet.")
                    .posterUrl(BASE + "c0nUX6Q1ZB0P2t1Jo6EeFSVnOGQ.jpg").tmdbId(399174).build(),
                List.of(wesAnderson),
                List.of(new CastEntry(bryanCranston, "Chief"), new CastEntry(edwardNorton, "Rex"), new CastEntry(billMurray, "Boss")));
            Person oliviaWilde = personRepo.save(Person.builder().name("Olivia Wilde").birthYear(1984).countryCode("US")
                    .biography("Actor-director known for Booksmart.").photoUrl(BASE + "eODi1QKamyVa41eSK2SjU20VAZS.jpg").build());
            Person kaitlynDever = personRepo.save(Person.builder().name("Kaitlyn Dever").birthYear(1996).countryCode("US")
                    .biography("Actress known for Booksmart and Unbelievable.").photoUrl(BASE + "np5rBCFRdeS1sXePL8uY09ktoC4.jpg").build());
            Person beanieFeldstein = personRepo.save(Person.builder().name("Beanie Feldstein").birthYear(1993).countryCode("US")
                    .biography("Actress known for Booksmart and Lady Bird.").photoUrl(BASE + "lyK1s3TcUKQidfVDnNPt3TAc6mI.jpg").build());
            saveMovie(Movie.builder().title("Booksmart").releaseYear(2019).genre(Genre.COMEDY).rating(7.0).runtime(102)
                    .plot("Two academic teenage superstars realize, on the eve of their high school graduation, that they should have worked less and played more. Determined to never fall short of their peers, the girls set out on a mission to cram four years of fun into one night.")
                    .posterUrl(BASE + "2aSxRDmisJP90H3S0aocyuQIe4z.jpg").tmdbId(505600).build(),
                List.of(oliviaWilde),
                List.of(new CastEntry(kaitlynDever, "Amy"), new CastEntry(beanieFeldstein, "Molly")));

            // SCI-FI
            saveMovie(Movie.builder().title("The Terminator").releaseYear(1984).genre(Genre.SCIFI).rating(8.1).runtime(107)
                    .plot("In the post-apocalyptic future, reigning tyrannical supercomputers teleport a cyborg assassin known as the \"Terminator\" back to 1984 to kill Sarah Connor, whose unborn son is destined to lead insurgents against 21st century mechanical hegemony. Meanwhile, the human-resistance movement dispatches a lone warrior to safeguard Sarah. Can he stop the virtually indestructible killing machine?")
                    .posterUrl(BASE + "qvktm0BHcnmDpul4Hz01GIazWPr.jpg").tmdbId(218).build(),
                List.of(jamesCameron),
                List.of(new CastEntry(arnoldSchwarzenegger, "T-800"), new CastEntry(lindaHamilton, "Sarah Connor")));
            saveMovie(Movie.builder().title("Terminator 2: Judgment Day").releaseYear(1991).genre(Genre.SCIFI).rating(8.6).runtime(137)
                    .plot("Ten years after the events of the original, a reprogrammed T-800 is sent back in time to protect young John Connor from the shape-shifting T-1000. Together with his mother Sarah, he fights to stop Skynet from triggering a nuclear apocalypse.")
                    .posterUrl(BASE + "jFTVD4XoWQTcg7wdyJKa8PEds5q.jpg").tmdbId(280).build(),
                List.of(jamesCameron),
                List.of(new CastEntry(arnoldSchwarzenegger, "T-800"), new CastEntry(lindaHamilton, "Sarah Connor")));
            saveMovie(Movie.builder().title("Terminator 3: Rise of the Machines").releaseYear(2003).genre(Genre.SCIFI).rating(6.3).runtime(109)
                    .plot("It's been 10 years since John Connor saved Earth from Judgment Day, and he's now living under the radar, steering clear of using anything Skynet can trace. That is, until he encounters T-X, a robotic assassin ordered to finish what T-1000 started. Good thing Connor's former nemesis, the Terminator, is back to aid the now-adult Connor … just like he promised.")
                    .posterUrl(BASE + "nvsoLAclNfpyJSp73TiGKwZoqJW.jpg").tmdbId(296).build(),
                List.of(jonathanMostow),
                List.of(new CastEntry(arnoldSchwarzenegger, "T-850"), new CastEntry(claireDanes, "Kate Brewster")));
            saveMovie(Movie.builder().title("Terminator Salvation").releaseYear(2009).genre(Genre.SCIFI).rating(6.5).runtime(115)
                    .plot("All grown up in post-apocalyptic 2018, John Connor must lead the resistance of humans against the increasingly dominating militaristic robots. But when Marcus Wright appears, his existence confuses the mission as Connor tries to determine whether Wright has come from the future or the past, and whether he's friend or foe.")
                    .posterUrl(BASE + "gw6JhlekZgtKUFlDTezq3j5JEPK.jpg").tmdbId(534).build(),
                List.of(mcg),
                List.of(new CastEntry(christianBale, "John Connor"), new CastEntry(samWorthington, "Marcus Wright")));
            saveMovie(Movie.builder().title("Terminator Genisys").releaseYear(2015).genre(Genre.SCIFI).rating(6.3).runtime(126)
                    .plot("The year is 2029. John Connor, leader of the resistance continues the war against the machines. At the Los Angeles offensive, John's fears of the unknown future begin to emerge when TECOM spies reveal a new plot by SkyNet that will attack him from both fronts; past and future, and will ultimately change warfare forever.")
                    .posterUrl(BASE + "oZRVDpNtmHk8M1VYy1aeOWUXgbC.jpg").tmdbId(87101).build(),
                List.of(alanTaylor),
                List.of(new CastEntry(arnoldSchwarzenegger, "Guardian"), new CastEntry(emiliaClarke, "Sarah Connor")));
            saveMovie(Movie.builder().title("Terminator: Dark Fate").releaseYear(2019).genre(Genre.SCIFI).rating(6.2).runtime(128)
                    .plot("Decades after Sarah Connor prevented Judgment Day, a lethal new Terminator is sent to eliminate the future leader of the resistance. In a fight to save mankind, battle-hardened Sarah Connor teams up with an unexpected ally and an enhanced super soldier to stop the deadliest Terminator yet.")
                    .posterUrl(BASE + "vqzNJRH4YyquRiWxCCOH0aXggHI.jpg").tmdbId(290859).build(),
                List.of(timMiller),
                List.of(new CastEntry(arnoldSchwarzenegger, "Carl"), new CastEntry(lindaHamilton, "Sarah Connor")));
            saveMovie(Movie.builder().title("Star Wars: Episode IV - A New Hope").releaseYear(1977).genre(Genre.SCIFI).rating(8.6).runtime(121)
                    .plot("Princess Leia is captured and held hostage by the evil Imperial forces in their effort to take over the galactic Empire. Venturesome Luke Skywalker and dashing captain Han Solo team together with the loveable robot duo R2-D2 and C-3PO to rescue the beautiful princess and restore peace and justice in the Empire.")
                    .posterUrl(BASE + "6FfCtAuVAW8XJjZ7eWeLibRLWTw.jpg").tmdbId(11).build(),
                List.of(georgeLucas),
                List.of(new CastEntry(harrisonFord, "Han Solo"), new CastEntry(markHamill, "Luke Skywalker"), new CastEntry(carrieFisher, "Leia Organa")));
            saveMovie(Movie.builder().title("Star Wars: Episode V - The Empire Strikes Back").releaseYear(1980).genre(Genre.SCIFI).rating(8.7).runtime(124)
                    .plot("The epic saga continues as Luke Skywalker, in hopes of defeating the evil Galactic Empire, learns the ways of the Jedi from aging master Yoda. But Darth Vader is more determined than ever to capture Luke. Meanwhile, rebel leader Princess Leia, cocky Han Solo, Chewbacca, and droids C-3PO and R2-D2 are thrown into various stages of capture, betrayal and despair.")
                    .posterUrl(BASE + "nNAeTmF4CtdSgMDplXTDPOpYzsX.jpg").tmdbId(1891).build(),
                List.of(irvinKershner),
                List.of(new CastEntry(harrisonFord, "Han Solo"), new CastEntry(markHamill, "Luke Skywalker"), new CastEntry(carrieFisher, "Leia Organa")));
            saveMovie(Movie.builder().title("Star Wars: Episode VI - Return of the Jedi").releaseYear(1983).genre(Genre.SCIFI).rating(8.3).runtime(131)
                    .plot("Luke Skywalker leads a mission to rescue his friend Han Solo from the clutches of Jabba the Hutt, the Emperor prepares to crush the Rebellion with a more powerful Death Star, and the Rebel fleet mounts a massive attack on the space station. Luke Skywalker confronts Darth Vader in a final climactic duel before the evil Emperor.")
                    .posterUrl(BASE + "jQYlydvHm3kUix1f8prMucrplhm.jpg").tmdbId(1892).build(),
                List.of(richardMarquand),
                List.of(new CastEntry(harrisonFord, "Han Solo"), new CastEntry(markHamill, "Luke Skywalker"), new CastEntry(carrieFisher, "Leia Organa")));
            saveMovie(Movie.builder().title("Star Wars: Episode I - The Phantom Menace").releaseYear(1999).genre(Genre.SCIFI).rating(6.5).runtime(136)
                    .plot("Anakin Skywalker, a young slave strong with the Force, is discovered on Tatooine. Meanwhile, the evil Sith have returned, enacting their plot for revenge against the Jedi.")
                    .posterUrl(BASE + "6wkfovpn7Eq8dYNKaG5PY3q2oq6.jpg").tmdbId(1893).build(),
                List.of(georgeLucas),
                List.of(new CastEntry(liamNeeson, "Qui-Gon Jinn"), new CastEntry(ewanMcGregor, "Obi-Wan Kenobi"), new CastEntry(nataliePortman, "Padmé Amidala")));
            saveMovie(Movie.builder().title("Star Wars: Episode II - Attack of the Clones").releaseYear(2002).genre(Genre.SCIFI).rating(6.6).runtime(142)
                    .plot("Following an assassination attempt on Senator Padmé Amidala, Jedi Knights Anakin Skywalker and Obi-Wan Kenobi investigate a mysterious plot into the heart of the Separatist movement and the beginning of the Clone Wars.")
                    .posterUrl(BASE + "oZNPzxqM2s5DyVWab09NTQScDQt.jpg").tmdbId(1894).build(),
                List.of(georgeLucas),
                List.of(new CastEntry(ewanMcGregor, "Obi-Wan Kenobi"), new CastEntry(nataliePortman, "Padmé Amidala")));
            saveMovie(Movie.builder().title("Star Wars: Episode III - Revenge of the Sith").releaseYear(2005).genre(Genre.SCIFI).rating(7.5).runtime(140)
                    .plot("When the sinister Sith unveil a thousand-year-old plot to rule the galaxy, the Republic crumbles and from its ashes rises the evil Galactic Empire. Jedi hero Anakin Skywalker must choose a side.")
                    .posterUrl(BASE + "xfSAoBEm9MNBjmlNcDYLvLSMlnq.jpg").tmdbId(1895).build(),
                List.of(georgeLucas),
                List.of(new CastEntry(ewanMcGregor, "Obi-Wan Kenobi"), new CastEntry(nataliePortman, "Padmé Amidala")));
            saveMovie(Movie.builder().title("Star Wars: Episode VII - The Force Awakens").releaseYear(2015).genre(Genre.SCIFI).rating(7.9).runtime(138)
                    .plot("Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new threat from the evil Kylo Ren and his army of Stormtroopers.")
                    .posterUrl(BASE + "wqnLdwVXoBjKibFRR5U3y0aDUhs.jpg").tmdbId(140607).build(),
                List.of(jJAbrams),
                List.of(new CastEntry(harrisonFord, "Han Solo"), new CastEntry(daisyRidley, "Rey"), new CastEntry(carrieFisher, "Leia Organa"), new CastEntry(adamDriver, "Kylo Ren")));
            saveMovie(Movie.builder().title("Star Wars: Episode VIII - The Last Jedi").releaseYear(2017).genre(Genre.SCIFI).rating(7.0).runtime(152)
                    .plot("Rey develops her newly discovered abilities with the guidance of Luke Skywalker, who is unsettled by the strength of her powers. Meanwhile, the Resistance prepares to do battle with the First Order.")
                    .posterUrl(BASE + "kOVEVeg59E0wsnXmF9nrh6OmWII.jpg").tmdbId(181808).build(),
                List.of(rianJohnson),
                List.of(new CastEntry(daisyRidley, "Rey"), new CastEntry(markHamill, "Luke Skywalker"), new CastEntry(adamDriver, "Kylo Ren")));
            saveMovie(Movie.builder().title("Star Wars: Episode IX - The Rise of Skywalker").releaseYear(2019).genre(Genre.SCIFI).rating(6.5).runtime(142)
                    .plot("The surviving Resistance faces the First Order once again as the journey of Rey, Finn and Poe Dameron continues. With the power and knowledge of generations behind them, the final battle begins.")
                    .posterUrl(BASE + "db32LaOibwEliAmSL2jjDF6oDdj.jpg").tmdbId(181812).build(),
                List.of(jJAbrams),
                List.of(new CastEntry(daisyRidley, "Rey"), new CastEntry(adamDriver, "Kylo Ren")));
            Movie inception = saveMovie(Movie.builder().title("Inception").releaseYear(2010).genre(Genre.SCIFI).rating(8.8).runtime(148)
                    .plot("Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets is offered a chance to regain his old life as payment for a task considered to be impossible: \"inception\", the implantation of another person's idea into a target's subconscious.")
                    .posterUrl(BASE + "xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg").tmdbId(27205).build(),
                List.of(christopherNolan),
                List.of(new CastEntry(leonardoDiCaprio, "Dom Cobb"), new CastEntry(tomHardy, "Eames")));
            saveMovie(Movie.builder().title("Interstellar").releaseYear(2014).genre(Genre.SCIFI).rating(8.6).runtime(169)
                    .plot("The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.")
                    .posterUrl(BASE + "yQvGrMoipbRoddT0ZR8tPoR7NfX.jpg").tmdbId(157336).build(),
                List.of(christopherNolan),
                List.of(new CastEntry(matthewMcConaughey, "Cooper"), new CastEntry(jessicaChastain, "Murph")));
            Movie theMatrix = saveMovie(Movie.builder().title("The Matrix").releaseYear(1999).genre(Genre.SCIFI).rating(8.7).runtime(136)
                    .plot("Set in the 22nd century, The Matrix tells the story of a computer hacker who joins a group of underground insurgents fighting the vast and powerful computers who now rule the earth.")
                    .posterUrl(BASE + "p96dm7sCMn4VYAStA6siNz30G1r.jpg").tmdbId(603).build(),
                List.of(lanaWachowski, lillyWachowski),
                List.of(new CastEntry(keanuReeves, "Neo"), new CastEntry(carrieAnneMoss, "Trinity")));
            saveMovie(Movie.builder().title("The Matrix Reloaded").releaseYear(2003).genre(Genre.SCIFI).rating(7.2).runtime(138)
                    .plot("The Resistance builds in numbers as humans are freed from the Matrix and brought to the city of Zion. Neo discovers his superpowers, including the ability to see the code inside the Matrix. With machine sentinels digging to Zion in 72 hours, Neo, Morpheus and Trinity must find the Keymaker to ultimately reach the Source.")
                    .posterUrl(BASE + "aA5qHS0FbSXO8PxcxUIHbDrJyuh.jpg").tmdbId(604).build(),
                List.of(lanaWachowski, lillyWachowski),
                List.of(new CastEntry(keanuReeves, "Neo"), new CastEntry(carrieAnneMoss, "Trinity")));
            saveMovie(Movie.builder().title("The Matrix Revolutions").releaseYear(2003).genre(Genre.SCIFI).rating(6.8).runtime(129)
                    .plot("The human city of Zion defends itself against the massive invasion of the machines as Neo fights to end the war at another front while also opposing the rogue Agent Smith.")
                    .posterUrl(BASE + "bkkS61w94ZVMNVd8KEyyJl2tnY5.jpg").tmdbId(605).build(),
                List.of(lanaWachowski, lillyWachowski),
                List.of(new CastEntry(keanuReeves, "Neo"), new CastEntry(carrieAnneMoss, "Trinity")));
            saveMovie(Movie.builder().title("The Matrix Resurrections").releaseYear(2021).genre(Genre.SCIFI).rating(5.7).runtime(148)
                    .plot("Plagued by strange memories, Neo's life takes an unexpected turn when he finds himself back inside the Matrix.")
                    .posterUrl(BASE + "8c4a8kE7PizaGQQnditMmI1xbRp.jpg").tmdbId(624860).build(),
                List.of(lanaWachowski),
                List.of(new CastEntry(keanuReeves, "Neo"), new CastEntry(carrieAnneMoss, "Trinity")));
            saveMovie(Movie.builder().title("Dune").releaseYear(2021).genre(Genre.SCIFI).rating(7.9).runtime(155)
                    .plot("Paul Atreides, a brilliant and gifted young man born into a great destiny beyond his understanding, must travel to the most dangerous planet in the universe to ensure the future of his family and his people. As malevolent forces explode into conflict over the planet's exclusive supply of the most precious resource in existence - a commodity capable of unlocking humanity's greatest potential - only those who can conquer their fear will survive.")
                    .posterUrl(BASE + "gDzOcq0pfeCeqMBwKIJlSmQpjkZ.jpg").tmdbId(438631).build(),
                List.of(denisVilleneuve),
                List.of(new CastEntry(timotheeChalamet, "Paul Atreides"), new CastEntry(zendaya, "Chani")));
            saveMovie(Movie.builder().title("Dune: Part Two").releaseYear(2024).genre(Genre.SCIFI).rating(8.5).runtime(167)
                    .plot("Follow the mythic journey of Paul Atreides as he unites with Chani and the Fremen while on a path of revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the known universe, Paul endeavors to prevent a terrible future only he can foresee.")
                    .posterUrl(BASE + "6izwz7rsy95ARzTR3poZ8H6c5pp.jpg").tmdbId(693134).build(),
                List.of(denisVilleneuve),
                List.of(new CastEntry(timotheeChalamet, "Paul Atreides"), new CastEntry(zendaya, "Chani")));

            // THRILLER / CRIME
            saveMovie(Movie.builder().title("Goodfellas").releaseYear(1990).genre(Genre.CRIME).rating(8.7).runtime(145)
                    .plot("The true story of Henry Hill, a half-Irish, half-Sicilian Brooklyn kid who is adopted by neighbourhood gangsters at an early age and climbs the ranks of a Mafia family under the guidance of Jimmy Conway.")
                    .posterUrl(BASE + "9OkCLM73MIU2CrKZbqiT8Ln1wY2.jpg").tmdbId(769).build(),
                List.of(martinScorsese),
                List.of(new CastEntry(rayLiotta, "Henry Hill"), new CastEntry(robertDeNiro, "Jimmy Conway")));
            saveMovie(Movie.builder().title("Se7en").releaseYear(1995).genre(Genre.THRILLER).rating(8.6).runtime(127)
                    .plot("Two homicide detectives are on a desperate hunt for a serial killer whose crimes are based on the \"seven deadly sins\" in this dark and haunting film that takes viewers from the tortured remains of one victim to the next. The seasoned Det. Somerset researches each sin in an effort to get inside the killer's mind, while his novice partner, Mills, scoffs at his efforts to unravel the case.")
                    .posterUrl(BASE + "191nKfP0ehp3uIvWqgPbFmI4lv9.jpg").tmdbId(807).build(),
                List.of(davidFincher),
                List.of(new CastEntry(bradPitt, "David Mills"), new CastEntry(morganFreeman, "William Somerset"), new CastEntry(kevinSpacey, "John Doe")));
            saveMovie(Movie.builder().title("The Departed").releaseYear(2006).genre(Genre.CRIME).rating(8.5).runtime(151)
                    .plot("To take down South Boston's Irish Mafia, the police send in one of their own to infiltrate the underworld, not realizing the syndicate has done likewise. While an undercover cop curries favor with the mob kingpin, a career criminal rises through the police ranks. But both sides soon discover there's a mole among them.")
                    .posterUrl(BASE + "nT97ifVT2J1yMQmeq20Qblg61T.jpg").tmdbId(1422).build(),
                List.of(martinScorsese),
                List.of(new CastEntry(leonardoDiCaprio, "Billy Costigan"), new CastEntry(jackNicholson, "Frank Costello")));
            saveMovie(Movie.builder().title("Knives Out").releaseYear(2019).genre(Genre.MYSTERY).rating(7.9).runtime(130)
                    .plot("When renowned crime novelist Harlan Thrombey is found dead at his estate just after his 85th birthday, the inquisitive and debonair Detective Benoit Blanc is mysteriously enlisted to investigate. From Harlan's dysfunctional family to his devoted staff, Blanc sifts through a web of red herrings and self-serving lies to uncover the truth behind Harlan's untimely death.")
                    .posterUrl(BASE + "pThyQovXQrw2m0s9x82twj48Jq4.jpg").tmdbId(546554).build(),
                List.of(rianJohnson),
                List.of(new CastEntry(danielCraig, "Benoit Blanc"), new CastEntry(anaDeArmas, "Marta Cabrera")));
            saveMovie(Movie.builder().title("The Silence of the Lambs").releaseYear(1991).genre(Genre.THRILLER).rating(8.6).runtime(118)
                    .plot("Clarice Starling is a top student at the FBI's training academy.  Jack Crawford wants Clarice to interview Dr. Hannibal Lecter, a brilliant psychiatrist who is also a violent psychopath, serving life behind bars for various acts of murder and cannibalism.  Crawford believes that Lecter may have insight into a case and that Starling, as an attractive young woman, may be just the bait to draw him out.")
                    .posterUrl(BASE + "uS9m8OBk1A8eM9I042bx8XXpqAq.jpg").tmdbId(274).build(),
                List.of(jonathanDemme),
                List.of(new CastEntry(anthonyHopkins, "Hannibal Lecter"), new CastEntry(jodieFoster, "Clarice Starling")));
            saveMovie(Movie.builder().title("Taxi Driver").releaseYear(1976).genre(Genre.CRIME).rating(8.3).runtime(114)
                    .plot("Suffering from insomnia, disturbed loner Travis Bickle takes a job as a New York City cabbie, haunting the streets nightly, growing increasingly detached from reality as he dreams of cleaning up the filthy city.")
                    .posterUrl(BASE + "ekstpH614fwDX8DUln1a2Opz0N8.jpg").tmdbId(103).build(),
                List.of(martinScorsese),
                List.of(new CastEntry(robertDeNiro, "Travis Bickle"), new CastEntry(jodieFoster, "Iris")));

            // HORROR
            saveMovie(Movie.builder().title("A Quiet Place").releaseYear(2018).genre(Genre.HORROR).rating(7.5).runtime(90)
                    .plot("A family is forced to live in silence while hiding from creatures that hunt by sound.")
                    .posterUrl(BASE + "nAU74GmpUk7t5iklEp3bufwDq4n.jpg").tmdbId(447332).build(),
                List.of(johnKrasinski),
                List.of(new CastEntry(emilyBlunt, "Evelyn Abbott"), new CastEntry(johnKrasinski, "Lee Abbott")));
            saveMovie(Movie.builder().title("The Shining").releaseYear(1980).genre(Genre.HORROR).rating(8.4).runtime(146)
                    .plot("Jack Torrance accepts a caretaker job at the Overlook Hotel, where he, along with his wife Wendy and their son Danny, must live isolated from the rest of the world for the winter. But they aren't prepared for the madness that lurks within.")
                    .posterUrl(BASE + "uAR0AWqhQL1hQa69UDEbb2rE5Wx.jpg").tmdbId(694).build(),
                List.of(stanleyKubrick),
                List.of(new CastEntry(jackNicholson, "Jack Torrance"), new CastEntry(shelleyDuvall, "Wendy Torrance")));
            saveMovie(Movie.builder().title("Alien").releaseYear(1979).genre(Genre.HORROR).rating(8.5).runtime(117)
                    .plot("During its return to the earth, commercial spaceship Nostromo intercepts a distress signal from a distant planet. When a three-member team of the crew discovers a chamber containing thousands of eggs on the planet, a creature inside one of the eggs attacks an explorer. The entire crew is unaware of the impending nightmare set to descend upon them when the alien parasite planted inside its unfortunate host is birthed.")
                    .posterUrl(BASE + "vfrQk5IPloGg1v9Rzbh2Eg3VGyM.jpg").tmdbId(348).build(),
                List.of(ridleyScott),
                List.of(new CastEntry(sigourneyWeaver, "Ellen Ripley"), new CastEntry(tomSkerritt, "Arthur Dallas")));
            saveMovie(Movie.builder().title("Psycho").releaseYear(1960).genre(Genre.HORROR).rating(8.5).runtime(109)
                    .plot("When larcenous real estate clerk Marion Crane goes on the lam with a wad of cash and hopes of starting a new life, she ends up at the notorious Bates Motel, where manager Norman Bates cares for his housebound mother.")
                    .posterUrl(BASE + "yz4QVqPx3h1hD1DfqqQkCq3rmxW.jpg").tmdbId(539).build(),
                List.of(alfredHitchcock),
                List.of(new CastEntry(anthonyPerkins, "Norman Bates"), new CastEntry(janetLeigh, "Marion Crane")));
            saveMovie(Movie.builder().title("Get Out").releaseYear(2017).genre(Genre.HORROR).rating(7.7).runtime(104)
                    .plot("Chris and his girlfriend Rose go upstate to visit her parents for the weekend. At first, Chris reads the family's overly accommodating behavior as nervous attempts to deal with their daughter's interracial relationship, but as the weekend progresses, a series of increasingly disturbing discoveries lead him to a truth that he never could have imagined.")
                    .posterUrl(BASE + "tFXcEccSQMf3lfhfXKSU9iRBpa3.jpg").tmdbId(419430).build(),
                List.of(jordanPeele),
                List.of(new CastEntry(danielKaluuya, "Chris Washington"), new CastEntry(allisonWilliams, "Rose Armitage")));
            saveMovie(Movie.builder().title("Hereditary").releaseYear(2018).genre(Genre.HORROR).rating(7.1).runtime(127)
                    .plot("Following the death of the Leigh family matriarch, Annie and her children uncover disturbing secrets about their heritage. Their daily lives are not only impacted, but they also become entangled in a chilling fate from which they cannot escape, driving them to the brink of madness.")
                    .posterUrl(BASE + "4GFPuL14eXi66V96xBWY73Y9PfR.jpg").tmdbId(493922).build(),
                List.of(ariAster),
                List.of(new CastEntry(toniCollette, "Annie Graham"), new CastEntry(alexWolff, "Peter Graham")));

            // ROMANCE
            saveMovie(Movie.builder().title("Casablanca").releaseYear(1942).genre(Genre.ROMANCE).rating(8.5).runtime(102)
                    .plot("In Casablanca, Morocco in December 1941, a cynical American expatriate meets a former lover, with unforeseen complications.")
                    .posterUrl(BASE + "lGCEKlJo2CnWydQj7aamY7s1S7Q.jpg").tmdbId(289).build(),
                List.of(michaelCurtiz),
                List.of(new CastEntry(humphreyBogart, "Rick Blaine"), new CastEntry(ingridBergman, "Ilsa Lund")));
            Movie laLaLand = saveMovie(Movie.builder().title("La La Land").releaseYear(2016).genre(Genre.ROMANCE).rating(7.9).runtime(128)
                    .plot("Mia, an aspiring actress, serves lattes to movie stars in between auditions and Sebastian, a jazz musician, scrapes by playing cocktail party gigs in dingy bars, but as success mounts they are faced with decisions that begin to fray the fragile fabric of their love affair, and the dreams they worked so hard to maintain in each other threaten to rip them apart.")
                    .posterUrl(BASE + "uDO8zWDhfWwoFdKS4fzkUJt0Rf0.jpg").tmdbId(313369).build(),
                List.of(damienChazelle),
                List.of(new CastEntry(ryanGosling, "Sebastian"), new CastEntry(emmaStone, "Mia")));
            saveMovie(Movie.builder().title("Eternal Sunshine of the Spotless Mind").releaseYear(2004).genre(Genre.ROMANCE).rating(8.3).runtime(108)
                    .plot("Joel Barish, heartbroken that his girlfriend underwent a procedure to erase him from her memory, decides to do the same. However, as he watches his memories of her fade away, he realises that he still loves her, and may be too late to correct his mistake.")
                    .posterUrl(BASE + "5MwkWH9tYHv3mV9OdYTMR5qreIz.jpg").tmdbId(38).build(),
                List.of(michelGondry),
                List.of(new CastEntry(jimCarrey, "Joel Barish"), new CastEntry(kateWinslet, "Clementine Kruczynski")));
            saveMovie(Movie.builder().title("When Harry Met Sally").releaseYear(1989).genre(Genre.ROMANCE).rating(7.6).runtime(96)
                    .plot("Sex always gets in the way of friendships between men and women. At least, that's what Harry Burns believes. So when Harry meets Sally Albright and a deep friendship blossoms between them, Harry's determined not to let his attraction to Sally destroy it. But when a night of weakness ends in a morning of panic, can the pair avoid succumbing to Harry's fears by remaining friends and admitting they just might be the perfect match for each other?")
                    .posterUrl(BASE + "rFOiFUhTMtDetqCGClC9PIgnC1P.jpg").tmdbId(639).build(),
                List.of(robReiner),
                List.of(new CastEntry(billyCrystal, "Harry Burns"), new CastEntry(megRyan, "Sally Albright")));

            // WAR
            saveMovie(Movie.builder().title("Dunkirk").releaseYear(2017).genre(Genre.WAR).rating(7.9).runtime(106)
                    .plot("The story of the miraculous evacuation of Allied soldiers from Belgium, Britain, Canada and France, who were cut off and surrounded by the German army from the beaches and harbour of Dunkirk between May 26th and June 4th 1940 during World War II.")
                    .posterUrl(BASE + "b4Oe15CGLL61Ped0RAS9JpqdmCt.jpg").tmdbId(374720).build(),
                List.of(christopherNolan),
                List.of(new CastEntry(aneurinBarnard, "Gibson"), new CastEntry(tomHardy, "Farrier")));

            // FANTASY
            saveMovie(Movie.builder().title("The Lord of the Rings: The Fellowship of the Ring").releaseYear(2001).genre(Genre.FANTASY).rating(8.8).runtime(178)
                    .plot("Young hobbit Frodo Baggins, after inheriting a mysterious ring from his uncle Bilbo, must leave his home in order to keep it from falling into the hands of its evil creator. Along the way, a fellowship is formed to protect the ringbearer and make sure that the ring arrives at its final destination: Mt. Doom, the only place where it can be destroyed.")
                    .posterUrl(BASE + "6oom5QYQ2yQTMJIbnvbkBL9cHo6.jpg").tmdbId(120).build(),
                List.of(peterJackson),
                List.of(new CastEntry(elijahWood, "Frodo Baggins"), new CastEntry(ianMcKellen, "Gandalf"), new CastEntry(cateBlanchett, "Galadriel")));
            saveMovie(Movie.builder().title("The Lord of the Rings: The Two Towers").releaseYear(2002).genre(Genre.FANTASY).rating(8.8).runtime(179)
                    .plot("Frodo Baggins and the other members of the Fellowship continue on their sacred quest to destroy the One Ring, but on separate paths. Their destinies lie at two towers: Orthanc Tower in Isengard, where the corrupt wizard Saruman awaits, and Sauron's fortress at Barad-dur, deep within the dark lands of Mordor.")
                    .posterUrl(BASE + "5VTN0pR8gcqV3EPUHHfMGnJYN9L.jpg").tmdbId(121).build(),
                List.of(peterJackson),
                List.of(new CastEntry(elijahWood, "Frodo Baggins"), new CastEntry(ianMcKellen, "Gandalf")));
            saveMovie(Movie.builder().title("The Lord of the Rings: The Return of the King").releaseYear(2003).genre(Genre.FANTASY).rating(9.0).runtime(201)
                    .plot("As armies mass for a final battle that will decide the fate of the world, and powerful, ancient forces of Light and Dark compete to determine the outcome, one member of the Fellowship of the Ring is revealed as the noble heir to the throne of the Kings of Men. Yet, the sole hope for triumph over evil lies with a brave hobbit, Frodo, who, accompanied by his loyal friend Sam and the hideous, wretched Gollum, ventures deep into the very dark heart of Mordor on his seemingly impossible quest to destroy the Ring of Power.")
                    .posterUrl(BASE + "rCzpDGLbOoPwLjy3OAm5NUPOTrC.jpg").tmdbId(122).build(),
                List.of(peterJackson),
                List.of(new CastEntry(elijahWood, "Frodo Baggins"), new CastEntry(ianMcKellen, "Gandalf"), new CastEntry(cateBlanchett, "Galadriel")));

            // WESTERN
            saveMovie(Movie.builder().title("The Good, the Bad and the Ugly").releaseYear(1966).genre(Genre.WESTERN).rating(8.8).runtime(178)
                    .plot("While the Civil War rages on between the Union and the Confederacy, three men (a quiet loner, a ruthless hitman, and a Mexican bandit) comb the American Southwest in search of a strongbox containing $200,000 in stolen gold.")
                    .posterUrl(BASE + "bX2xnavhMYjWDoZp1VM6VnU1xwe.jpg").tmdbId(429).build(),
                List.of(sergioLeone),
                List.of(new CastEntry(clintEastwood, "Blondie"), new CastEntry(eliWallach, "Tuco")));
            saveMovie(Movie.builder().title("Unforgiven").releaseYear(1992).genre(Genre.WESTERN).rating(8.2).runtime(130)
                    .plot("William Munny is a retired, once-ruthless killer turned gentle widower and hog farmer. To help support his two motherless children, he accepts one last bounty-hunter mission to find the men who brutalized a prostitute. Joined by his former partner and a cocky greenhorn, he takes on a corrupt sheriff.")
                    .posterUrl(BASE + "54roTwbX9fltg85zjsmrooXAs12.jpg").tmdbId(33).build(),
                List.of(clintEastwood),
                List.of(new CastEntry(clintEastwood, "William Munny"), new CastEntry(morganFreeman, "Ned Logan")));
            saveMovie(Movie.builder().title("Million Dollar Baby").releaseYear(2004).genre(Genre.DRAMA).rating(8.1).runtime(132)
                    .plot("Despondent over a painful estrangement from his daughter, trainer Frankie Dunn isn't prepared for boxer Maggie Fitzgerald to enter his life. But Maggie's determined to go pro and to convince Dunn and his cohort to help her.")
                    .posterUrl(BASE + "jcfEqKdWF1zeyvECPqp3mkWLct2.jpg").tmdbId(70).build(),
                List.of(clintEastwood),
                List.of(new CastEntry(clintEastwood, "Frankie Dunn"), new CastEntry(morganFreeman, "Eddie 'Scrap-Iron' Dupris")));
            saveMovie(Movie.builder().title("Gran Torino").releaseYear(2008).genre(Genre.DRAMA).rating(8.1).runtime(116)
                    .plot("Disgruntled Korean War veteran Walt Kowalski sets out to reform his neighbor, Thao Lor, a Hmong teenager who tried to steal Kowalski's prized possession: a 1972 Gran Torino.")
                    .posterUrl(BASE + "zUybYvxWdAJy5hhYovsXtHSWI1l.jpg").tmdbId(13223).build(),
                List.of(clintEastwood),
                List.of(new CastEntry(clintEastwood, "Walt Kowalski")));

            // ── TV Shows ──────────────────────────────────────────────────
            TvShow friends = saveTvShow(TvShow.builder().title("Friends").genre(Genre.COMEDY).rating(8.9)
                    .plot("Six friends navigate life, love, and careers in New York City.")
                    .posterUrl(BASE + "2koX1xLkpTQM4IZebYvKysFW1Nh.jpg").tmdbId(1668).startYear(1994).endYear(2004).seasons(10).build(),
                List.of(davidCrane, martaKauffman),
                List.of(new CastEntry(jenniferAniston, "Rachel Green"), new CastEntry(courteneyCox, "Monica Geller"),
                        new CastEntry(lisaKudrow, "Phoebe Buffay"), new CastEntry(mattLeBlanc, "Joey Tribbiani"),
                        new CastEntry(matthewPerry, "Chandler Bing"), new CastEntry(davidSchwimmer, "Ross Geller")));
            episodeRepo.saveAll(List.of(
                Episode.builder().tvShow(friends).seasonNumber(1).episodeNumber(1).runtime(22).airYear(1994)
                    .title("The One Where Monica Gets a Roommate").overview("Rachel joins the group after leaving her fiancé at the altar.").build(),
                Episode.builder().tvShow(friends).seasonNumber(1).episodeNumber(7).runtime(22).airYear(1994)
                    .title("The One with the Blackout").overview("A city-wide blackout traps Chandler in an ATM vestibule with a model.").build(),
                Episode.builder().tvShow(friends).seasonNumber(1).episodeNumber(24).runtime(22).airYear(1995)
                    .title("The One Where Rachel Finds Out").overview("Rachel discovers Ross's feelings for her right as he leaves for China.").build(),
                Episode.builder().tvShow(friends).seasonNumber(2).episodeNumber(1).runtime(22).airYear(1995)
                    .title("The One with Ross's New Girlfriend").overview("Ross returns from China with a new girlfriend.").build(),
                Episode.builder().tvShow(friends).seasonNumber(2).episodeNumber(14).runtime(22).airYear(1996)
                    .title("The One with the Prom Video").overview("An old video reveals Ross was ready to be Rachel's prom date.").build(),
                Episode.builder().tvShow(friends).seasonNumber(2).episodeNumber(24).runtime(22).airYear(1996)
                    .title("The One with Barry and Mindy's Wedding").overview("Rachel is a bridesmaid at her ex's wedding.").build(),
                Episode.builder().tvShow(friends).seasonNumber(3).episodeNumber(1).runtime(22).airYear(1996)
                    .title("The One with the Princess Leia Fantasy").overview("Ross shares a secret fantasy with Rachel.").build(),
                Episode.builder().tvShow(friends).seasonNumber(3).episodeNumber(16).runtime(22).airYear(1997)
                    .title("The One the Morning After").overview("Ross and Rachel have a dramatic breakup.").build(),
                Episode.builder().tvShow(friends).seasonNumber(4).episodeNumber(1).runtime(22).airYear(1997)
                    .title("The One with the Jellyfish").overview("The gang reunites after summer apart.").build(),
                Episode.builder().tvShow(friends).seasonNumber(4).episodeNumber(24).runtime(44).airYear(1998)
                    .title("The One with Ross's Wedding").overview("Ross says the wrong name at the altar.").build(),
                Episode.builder().tvShow(friends).seasonNumber(5).episodeNumber(1).runtime(22).airYear(1998)
                    .title("The One After Ross Says Rachel").overview("The aftermath of Ross's wedding disaster.").build(),
                Episode.builder().tvShow(friends).seasonNumber(5).episodeNumber(14).runtime(22).airYear(1999)
                    .title("The One Where Everybody Finds Out").overview("Everyone discovers Chandler and Monica's relationship.").build(),
                Episode.builder().tvShow(friends).seasonNumber(6).episodeNumber(1).runtime(22).airYear(1999)
                    .title("The One After Vegas").overview("The gang returns from Las Vegas.").build(),
                Episode.builder().tvShow(friends).seasonNumber(6).episodeNumber(25).runtime(44).airYear(2000)
                    .title("The One with the Proposal").overview("Chandler proposes to Monica.").build(),
                Episode.builder().tvShow(friends).seasonNumber(7).episodeNumber(1).runtime(22).airYear(2000)
                    .title("The One with Monica's Thunder").overview("Monica and Chandler announce their engagement.").build(),
                Episode.builder().tvShow(friends).seasonNumber(7).episodeNumber(24).runtime(44).airYear(2001)
                    .title("The One with Chandler and Monica's Wedding").overview("Chandler and Monica finally get married.").build(),
                Episode.builder().tvShow(friends).seasonNumber(8).episodeNumber(1).runtime(22).airYear(2001)
                    .title("The One After I Do").overview("The aftermath of the wedding and Rachel's pregnancy reveal.").build(),
                Episode.builder().tvShow(friends).seasonNumber(8).episodeNumber(24).runtime(44).airYear(2002)
                    .title("The One Where Rachel Has a Baby").overview("Rachel gives birth to Emma.").build(),
                Episode.builder().tvShow(friends).seasonNumber(9).episodeNumber(1).runtime(22).airYear(2002)
                    .title("The One Where No One Proposes").overview("Joey's accidental proposal causes confusion.").build(),
                Episode.builder().tvShow(friends).seasonNumber(9).episodeNumber(23).runtime(44).airYear(2003)
                    .title("The One in Barbados").overview("The gang travels to Barbados for a conference.").build(),
                Episode.builder().tvShow(friends).seasonNumber(10).episodeNumber(1).runtime(22).airYear(2003)
                    .title("The One After Joey and Rachel Kiss").overview("The fallout from Joey and Rachel's kiss.").build(),
                Episode.builder().tvShow(friends).seasonNumber(10).episodeNumber(17).runtime(44).airYear(2004)
                    .title("The Last One").overview("Monica and Chandler prepare to move; Ross races to win Rachel back.").build()));

            TvShow seinfeld = saveTvShow(TvShow.builder().title("Seinfeld").genre(Genre.COMEDY).rating(8.8)
                    .plot("A stand-up comedian and his neurotic friends deal with the mundane aspects of everyday life in New York.")
                    .posterUrl(BASE + "aCw8ONfyz3AhngVQa1E2Ss4KSUQ.jpg").tmdbId(1400).startYear(1989).endYear(1998).seasons(9).build(),
                List.of(larryDavid, jerrySeinfeld),
                List.of(new CastEntry(jerrySeinfeld, "Jerry Seinfeld"), new CastEntry(juliaLouisDreyfus, "Elaine Benes"),
                        new CastEntry(jasonAlexander, "George Costanza"), new CastEntry(michaelRichards, "Cosmo Kramer")));
            episodeRepo.saveAll(List.of(
                Episode.builder().tvShow(seinfeld).seasonNumber(1).episodeNumber(1).runtime(23).airYear(1989)
                    .title("The Seinfeld Chronicles").overview("Jerry tries to figure out what a woman's behavior means.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(2).episodeNumber(1).runtime(23).airYear(1991)
                    .title("The Ex-Girlfriend").overview("George tries to retrieve a book from his ex-girlfriend.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(3).episodeNumber(1).runtime(23).airYear(1991)
                    .title("The Note").overview("The gang tries to get a massage covered by insurance.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(3).episodeNumber(23).runtime(23).airYear(1992)
                    .title("The Keys").overview("Kramer moves to Hollywood to pursue acting.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(4).episodeNumber(1).runtime(23).airYear(1992)
                    .title("The Trip").overview("Jerry and George fly to L.A. to find Kramer.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(4).episodeNumber(11).runtime(23).airYear(1992)
                    .title("The Contest").overview("The gang makes a bet to see who can go the longest without self-gratification.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(4).episodeNumber(20).runtime(23).airYear(1993)
                    .title("The Junior Mint").overview("Kramer accidentally drops a Junior Mint into a patient during surgery.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(5).episodeNumber(1).runtime(23).airYear(1993)
                    .title("The Mango").overview("Jerry learns Elaine has been faking it during their relationship.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(5).episodeNumber(21).runtime(23).airYear(1994)
                    .title("The Opposite").overview("George decides to do the opposite of every instinct he has.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(6).episodeNumber(1).runtime(23).airYear(1994)
                    .title("The Chaperone").overview("Jerry dates a Miss America contestant.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(6).episodeNumber(12).runtime(23).airYear(1995)
                    .title("The Label Maker").overview("Jerry is given a label maker as a re-gift.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(7).episodeNumber(1).runtime(23).airYear(1995)
                    .title("The Engagement").overview("George decides to propose to his ex-girlfriend Susan.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(7).episodeNumber(24).runtime(23).airYear(1996)
                    .title("The Invitations").overview("Susan dies from licking toxic wedding invitation envelopes.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(8).episodeNumber(1).runtime(23).airYear(1996)
                    .title("The Foundation").overview("The gang deals with Susan's death and a new foundation in her name.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(8).episodeNumber(22).runtime(23).airYear(1997)
                    .title("The Summer of George").overview("George plans to make the most of his severance pay.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(9).episodeNumber(1).runtime(23).airYear(1997)
                    .title("The Butter Shave").overview("Kramer starts shaving with butter.").build(),
                Episode.builder().tvShow(seinfeld).seasonNumber(9).episodeNumber(23).runtime(75).airYear(1998)
                    .title("The Finale").overview("Jerry, George, Elaine, and Kramer are put on trial for violating a Good Samaritan law.").build()));

            TvShow got = saveTvShow(TvShow.builder().title("Game of Thrones").genre(Genre.FANTASY).rating(9.2)
                    .plot("Nine noble families fight for control over the mythical lands of Westeros while an ancient enemy returns.")
                    .posterUrl(BASE + "1XS1oqL89opfnbLl8WnZY1O1uJx.jpg").tmdbId(1399).startYear(2011).endYear(2019).seasons(8).build(),
                List.of(davidBenioff, dBWeiss),
                List.of(new CastEntry(emiliaClarke, "Daenerys Targaryen"), new CastEntry(kitHarington, "Jon Snow"), new CastEntry(peterDinklage, "Tyrion Lannister")));
            episodeRepo.saveAll(List.of(
                Episode.builder().tvShow(got).seasonNumber(1).episodeNumber(1).runtime(62).airYear(2011)
                    .title("Winter Is Coming").overview("Lord Stark is asked to be the King's Hand and discovers a dark secret.").build(),
                Episode.builder().tvShow(got).seasonNumber(1).episodeNumber(9).runtime(57).airYear(2011)
                    .title("Baelor").overview("Ned Stark faces execution; Robb leads his army south.").build(),
                Episode.builder().tvShow(got).seasonNumber(1).episodeNumber(10).runtime(53).airYear(2011)
                    .title("Fire and Blood").overview("The realm reacts to Ned's execution; Daenerys emerges from the flames.").build(),
                Episode.builder().tvShow(got).seasonNumber(2).episodeNumber(1).runtime(53).airYear(2012)
                    .title("The North Remembers").overview("Joffrey celebrates his birthday; Stannis plans his claim to the throne.").build(),
                Episode.builder().tvShow(got).seasonNumber(2).episodeNumber(9).runtime(59).airYear(2012)
                    .title("Blackwater").overview("Stannis's fleet attacks King's Landing in a massive battle.").build(),
                Episode.builder().tvShow(got).seasonNumber(3).episodeNumber(1).runtime(52).airYear(2013)
                    .title("Valar Dohaeris").overview("Jon is brought before Mance Rayder; Daenerys sails for Slaver's Bay.").build(),
                Episode.builder().tvShow(got).seasonNumber(3).episodeNumber(9).runtime(52).airYear(2013)
                    .title("The Rains of Castamere").overview("The Red Wedding devastates the Stark forces.").build(),
                Episode.builder().tvShow(got).seasonNumber(4).episodeNumber(1).runtime(51).airYear(2014)
                    .title("Two Swords").overview("Tyrion welcomes a new Lannister ally; Jon prepares for war.").build(),
                Episode.builder().tvShow(got).seasonNumber(4).episodeNumber(8).runtime(52).airYear(2014)
                    .title("The Mountain and the Viper").overview("Oberyn Martell fights the Mountain as Tyrion's champion.").build(),
                Episode.builder().tvShow(got).seasonNumber(5).episodeNumber(1).runtime(50).airYear(2015)
                    .title("The Wars to Come").overview("Cersei sees the new world order; Jon weighs his options.").build(),
                Episode.builder().tvShow(got).seasonNumber(5).episodeNumber(8).runtime(57).airYear(2015)
                    .title("Hardhome").overview("Jon Snow fights the White Walkers at Hardhome.").build(),
                Episode.builder().tvShow(got).seasonNumber(6).episodeNumber(1).runtime(50).airYear(2016)
                    .title("The Red Woman").overview("The Night's Watch faces consequences; Sansa and Theon flee.").build(),
                Episode.builder().tvShow(got).seasonNumber(6).episodeNumber(9).runtime(60).airYear(2016)
                    .title("Battle of the Bastards").overview("Jon and Sansa reclaim Winterfell from Ramsay Bolton.").build(),
                Episode.builder().tvShow(got).seasonNumber(7).episodeNumber(1).runtime(59).airYear(2017)
                    .title("Dragonstone").overview("Jon organizes the North's defenses; Daenerys arrives at Dragonstone.").build(),
                Episode.builder().tvShow(got).seasonNumber(7).episodeNumber(7).runtime(79).airYear(2017)
                    .title("The Dragon and the Wolf").overview("A meeting in King's Landing; Jon and Daenerys's relationship deepens.").build(),
                Episode.builder().tvShow(got).seasonNumber(8).episodeNumber(1).runtime(54).airYear(2019)
                    .title("Winterfell").overview("The forces of men prepare for the final battle against the dead.").build(),
                Episode.builder().tvShow(got).seasonNumber(8).episodeNumber(3).runtime(82).airYear(2019)
                    .title("The Long Night").overview("The Battle of Winterfell against the Night King.").build(),
                Episode.builder().tvShow(got).seasonNumber(8).episodeNumber(6).runtime(80).airYear(2019)
                    .title("The Iron Throne").overview("The Starks decide the fate of the Seven Kingdoms.").build()));

            // ── Reviews (max one per user per title) ──────────────────────
            reviewRepo.saveAll(List.of(
                // The Shawshank Redemption: 8 reviews, recurring themes (hope, slow first hour, narration)
                Review.builder().user(admin).movie(shawshank).score(10)
                    .comment("The slow first hour is the whole point. By the time the tunnel appears you have lived every year with him, and the payoff lands because it was earned instead of staged.").build(),
                Review.builder().user(henrik).movie(shawshank).score(9)
                    .comment("Freeman's narration carries this. Without it the middle stretch would drag, and with it the prison feels like somewhere you have actually been.").build(),
                Review.builder().user(mara).movie(shawshank).score(10)
                    .comment("A film about hope that never once feels sentimental. The first hour is deliberately patient and I understand why some find it slow.").build(),
                Review.builder().user(petra).movie(shawshank).score(8)
                    .comment("Beautifully made, though the pacing in the first act tested me on rewatch. The narration is what pulls it through.").build(),
                Review.builder().user(dan).movie(shawshank).score(10)
                    .comment("Hope is the subject and the structure. Everything in the first hour is setup for a payoff that would not work without it.").build(),
                Review.builder().user(ines).movie(shawshank).score(9)
                    .comment("Freeman narrating a story that is not about him is the trick that makes it work. Slow to start and worth the patience.").build(),
                Review.builder().user(tomas).movie(shawshank).score(9)
                    .comment("The prison feels lived in. It takes its time, sometimes too much, but the ending is one of the few that genuinely earns its optimism.").build(),
                Review.builder().user(sofia).movie(shawshank).score(10)
                    .comment("I put off watching it for years because of the reputation. The reputation is correct. Hope, patience, and a narrator who knows exactly when to stop talking.").build(),

                // Inception: 6 reviews, recurring themes (the ending, the score, practical effects)
                Review.builder().user(admin).movie(inception).score(9)
                    .comment("Still arguing about the ending a decade later, which is either the point or the flaw depending on your mood. The score does an enormous amount of the work.").build(),
                Review.builder().user(mara).movie(inception).score(8)
                    .comment("The rotating corridor is practical and you can feel it. The ending is a cheap trick that I nonetheless think about constantly.").build(),
                Review.builder().user(dan).movie(inception).score(9)
                    .comment("Zimmer's score is the reason the last forty minutes work. The exposition in the first act is heavy, and the ambiguity at the end is the right call.").build(),
                Review.builder().user(henrik).movie(inception).score(7)
                    .comment("Too much of the first hour is characters explaining rules to each other. The practical effects and the score carry it past that.").build(),
                Review.builder().user(lucia).movie(inception).score(9)
                    .comment("The spinning top is not the interesting question. The score, the corridor fight, and the pacing of the final act are.").build(),
                Review.builder().user(noor).movie(inception).score(8)
                    .comment("Front-loaded with exposition and worth it. The practical corridor sequence still looks better than most digital work since.").build(),

                // The Matrix: 5 reviews, recurring themes (bullet time, ageing, the sequels)
                Review.builder().user(user).movie(theMatrix).score(9)
                    .comment("Aged remarkably well. Bullet time became a joke through imitation, but in the original it is used sparingly and it still reads as new.").build(),
                Review.builder().user(petra).movie(theMatrix).score(9)
                    .comment("The world building is tighter than I remembered. Try to ignore that the sequels exist and it is close to perfect.").build(),
                Review.builder().user(tomas).movie(theMatrix).score(8)
                    .comment("Bullet time is the thing everyone remembers and the practical stunt work is what actually holds up.").build(),
                Review.builder().user(viktor).movie(theMatrix).score(10)
                    .comment("Twenty-five years on and the first act still works as pure mystery. The sequels do not diminish it.").build(),
                Review.builder().user(sofia).movie(theMatrix).score(8)
                    .comment("Some of the dialogue is very of its time. The action and the central idea have aged far better than the script.").build(),

                // The Godfather: 2 reviews, deliberately below the summary threshold
                Review.builder().user(user).movie(godfather).score(10)
                    .comment("An offer you cannot refuse, and a first hour of wedding scenes that teaches you every family relationship you need for the next two.").build(),
                Review.builder().user(ines).movie(godfather).score(9)
                    .comment("The lighting alone is worth it. Slow in the middle and the ending justifies every minute.").build(),

                // Groundhog Day: 4 reviews, recurring themes (the loop as a growth device, Murray's tonal shift)
                Review.builder().user(henrik).movie(groundhogDay).score(8)
                    .comment("The premise could have stayed a gimmick. Instead the loop becomes the character's growth arc, and Murray plays the shift from cynical to decent without losing the deadpan.").build(),
                Review.builder().user(lucia).movie(groundhogDay).score(9)
                    .comment("Murray sells every stage of the loop, from denial to boredom to something close to grace, and the comic tone stays intact through all of it.").build(),
                Review.builder().user(noor).movie(groundhogDay).score(8)
                    .comment("A comedy about repetition that earns its warmth by the end. Rita's patience with the town is what the loop is actually testing in Phil.").build(),
                Review.builder().user(viktor).movie(groundhogDay).score(9)
                    .comment("The structure is simple and the execution is not. Each pass through the day adds one more detail to Phil's arc, until the change feels inevitable instead of forced.").build(),

                // La La Land: 3 reviews, recurring themes (the bittersweet ending, the score, the leads' chemistry)
                Review.builder().user(mara).movie(laLaLand).score(8)
                    .comment("The ending is the whole movie in miniature: bittersweet and honestly ambiguous about whether the dream was worth the choice. Gosling and Stone sell every beat of it.").build(),
                Review.builder().user(petra).movie(laLaLand).score(8)
                    .comment("The score does as much storytelling as the dialogue, especially in the closing sequence where the two of them imagine the life they did not choose.").build(),
                Review.builder().user(dan).movie(laLaLand).score(9)
                    .comment("Gosling and Stone have the kind of chemistry that makes the ending land instead of feel like a downer, and the jazz numbers are where the film is most alive.").build(),

                // TV shows keep their original single reviews
                Review.builder().user(admin).tvShow(friends).score(8)
                    .comment("Comfort TV at its finest.").build(),
                Review.builder().user(user).tvShow(seinfeld).score(9)
                    .comment("The blueprint for every sitcom since.").build(),
                Review.builder().user(admin).tvShow(got).score(7)
                    .comment("Amazing until the final season.").build()));

            // ── Watch lists (max one entry per user per title) ───────────
            watchlistRepo.saveAll(List.of(
                WatchlistItem.builder().user(admin).movie(inception).status(WatchStatus.WATCHED).build(),
                WatchlistItem.builder().user(admin).tvShow(got).status(WatchStatus.WANT_TO_WATCH).build(),
                WatchlistItem.builder().user(user).movie(theMatrix).status(WatchStatus.WATCHED).build(),
                WatchlistItem.builder().user(user).movie(shawshank).status(WatchStatus.WANT_TO_WATCH).build(),
                WatchlistItem.builder().user(user).tvShow(seinfeld).status(WatchStatus.WANT_TO_WATCH).build()));

                log.info("Seeding complete. {} movies, {} TV shows and {} reviews created.",
                    movieRepo.count(), tvShowRepo.count(), reviewRepo.count());
            }
        };
    }


    private Movie saveMovie(Movie movie, List<Person> directors, List<CastEntry> cast) {
        movie.getDirectors().addAll(directors);
        movieRepo.save(movie);
        movieCastRepo.saveAll(cast.stream()
            .map(entry -> MovieCast.builder().movie(movie).person(entry.person()).characterName(entry.characterName()).build())
            .toList());
        return movie;
    }

    private TvShow saveTvShow(TvShow show, List<Person> creators, List<CastEntry> cast) {
        show.getCreators().addAll(creators);
        tvShowRepo.save(show);
        tvShowCastRepo.saveAll(cast.stream()
            .map(entry -> TvShowCast.builder().tvShow(show).person(entry.person()).characterName(entry.characterName()).build())
            .toList());
        return show;
    }

}
