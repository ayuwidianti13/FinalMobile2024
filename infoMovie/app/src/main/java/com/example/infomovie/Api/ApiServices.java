package com.example.infomovie.Api;

import com.example.infomovie.Model.MovieModels;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiServices {
    String RAPID_API_KEY = "aec4a3d69fmsha7ac21b3c35a923p13c6a3jsn2d17160e124b";
    String RAPID_API_HOST = "imdb-top-100-movies.p.rapidapi.com";

    @Headers({
            "X-RapidAPI-Key: " + RAPID_API_KEY,
            "X-RapidAPI-Host: " + RAPID_API_HOST
    })
    @GET("/")
    Call<List<MovieModels>> getMovie();
}
