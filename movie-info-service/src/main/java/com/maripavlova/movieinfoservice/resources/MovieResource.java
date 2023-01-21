package com.maripavlova.movieinfoservice.resources;

import com.maripavlova.movieinfoservice.models.Movie;
import com.maripavlova.movieinfoservice.models.MovieSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId){
        //doesn't work with russian ip-address either with vpn on ide
     /*   String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
        String url = "https://api.themoviedb.org/3/movie/550?api_key=ed1b48c4ed44157be17e08ac95efd2a1";
        System.out.println("=================================== " + url);
        MovieSummary movieSummary = restTemplate.getForObject(
                url,
                MovieSummary.class
         );
        return new Movie(movieId, movieSummary.getTitle(), movieSummary.getOverview());*/
        return new Movie(movieId, "test movie name", "test movie description");

    }
}
