package com.maripavlova.moviecatalogservice.resources;

import com.maripavlova.moviecatalogservice.models.CatalogItem;
import com.maripavlova.moviecatalogservice.models.Movie;
import com.maripavlova.moviecatalogservice.models.Rating;
import com.maripavlova.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/{userId}")
//    @CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {


        //get all rated movies for userId from rating server
        UserRating ratings = getUserRating(userId);

        //for each rated movie of userid call movie info service to get details of movies
        return ratings.getUserRating().stream()
                        .map(rating -> {
                            //for each rated movie of userid call movie info service to get details of movies
                            return getCatalogItem(rating);
                        })
                        .collect(Collectors.toList());
    }

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem")
    private CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
        //put ratings and movies details together
        return new CatalogItem(movie.getName(), "description of movie", rating.getRating());
    }

    private CatalogItem getFallbackCatalogItem(Rating rating) {
        return new CatalogItem("Movie name not found", "Movie description not found", rating.getRating());
    }

    @HystrixCommand(fallbackMethod = "getFallbackUserRating")
    private UserRating getUserRating(String userId) {
        return restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);
    }

    private UserRating getFallbackUserRating(String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setUserRating(Arrays.asList(new Rating("0", 0)));
        return userRating;
    }



    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId) {
        System.out.println("====================================" + "I am in fallback method");
        return Arrays.asList(new CatalogItem("default_name", "default_description", 0));
    }
}


//                            Movie movie = webClient.build()
//                                    .get()
//                                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
//                                    .retrieve()
//                                    .bodyToMono(Movie.class)
//                                    .block();