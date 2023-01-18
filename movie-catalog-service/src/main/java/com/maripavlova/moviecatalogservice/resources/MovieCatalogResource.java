package com.maripavlova.moviecatalogservice.resources;

import com.maripavlova.moviecatalogservice.models.CatalogItem;
import com.maripavlova.moviecatalogservice.models.Movie;
import com.maripavlova.moviecatalogservice.models.Rating;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        RestTemplate restTemplate = new RestTemplate();


        //get all rated movies for userId from rating server
        List<Rating> ratings = Arrays.asList(
            new Rating("movieid1", 4),
            new Rating("movieid2", 5),
            new Rating("movieid3", 6)
        );

        //for each rated movie of userid call movie info service to get details of movies
        return ratings.stream()
                        .map(rating -> {
                            Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
                            return new CatalogItem(movie.getName(), "description of movie", rating.getRating());
                        })
                        .collect(Collectors.toList());

        //put ratings and movies details together

    }
}
