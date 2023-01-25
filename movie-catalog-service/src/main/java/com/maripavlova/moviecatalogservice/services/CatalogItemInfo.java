package com.maripavlova.moviecatalogservice.services;

import com.maripavlova.moviecatalogservice.models.CatalogItem;
import com.maripavlova.moviecatalogservice.models.Movie;
import com.maripavlova.moviecatalogservice.models.Rating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CatalogItemInfo {
    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem",
                    threadPoolKey = "movieInfoPool",
                    threadPoolProperties = {
                            @HystrixProperty(name="coreSize", value="20"),
                            @HystrixProperty(name="maxQueueSize", value="10")
                    }
    )
    public CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
        //put ratings and movies details together
        return new CatalogItem(movie.getName(), "description of movie", rating.getRating());
    }

    public CatalogItem getFallbackCatalogItem(Rating rating) {
        return new CatalogItem("Movie name not found", "Movie description not found", rating.getRating());
    }


}
