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
    @HystrixCommand(fallbackMethod = "getFallbackCatalog")
//    @CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {


        //get all rated movies for userId from rating server
        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);

        //for each rated movie of userid call movie info service to get details of movies
        return ratings.getUserRating().stream()
                        .map(rating -> {
                            //for each rated movie of userid call movie info service to get details of movies
                            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
                            //put ratings and movies details together
                            return new CatalogItem(movie.getName(), "description of movie", rating.getRating());
                        })
                        .collect(Collectors.toList());
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