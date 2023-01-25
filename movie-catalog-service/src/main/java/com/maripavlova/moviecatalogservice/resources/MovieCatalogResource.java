package com.maripavlova.moviecatalogservice.resources;

import com.maripavlova.moviecatalogservice.models.CatalogItem;
import com.maripavlova.moviecatalogservice.models.UserRating;
import com.maripavlova.moviecatalogservice.services.CatalogItemInfo;
import com.maripavlova.moviecatalogservice.services.UserRatingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
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

    @Autowired
    private UserRatingInfo userRatingInfo;

    @Autowired
    private CatalogItemInfo catalogItemInfo;

    @RequestMapping("/{userId}")
//    @CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {


        //get all rated movies for userId from rating server
        UserRating ratings = userRatingInfo.getUserRating(userId);

        //for each rated movie of userid call movie info service to get details of movies
        return ratings.getUserRating().stream()
                        .map(rating -> {
                            //for each rated movie of userid call movie info service to get details of movies
                            return catalogItemInfo.getCatalogItem(rating);
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