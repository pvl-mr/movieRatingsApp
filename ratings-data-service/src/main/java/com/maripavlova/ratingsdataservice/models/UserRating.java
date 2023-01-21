package com.maripavlova.ratingsdataservice.models;

import java.util.Arrays;
import java.util.List;

public class UserRating {
    private String userId;
    private List<Rating> userRating;

    public List<Rating> getUserRating() {
        return userRating;
    }

    public void setUserRating(List<Rating> userRating) {
        this.userRating = userRating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void initData(String userId){
        List<Rating> ratings = Arrays.asList(
                new Rating("100", 4),
                new Rating("200", 5),
                new Rating("300", 6)
        );
        this.setUserId(userId);
        this.setUserRating(ratings);
    }
}
