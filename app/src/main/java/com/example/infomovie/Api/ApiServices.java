package com.example.infomovie.Api;

import com.example.infomovie.Model.DetailModel;
import com.example.infomovie.Model.MovieModels;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServices {
    String RAPID_API_KEY = "d1e36f4c96msh8565fc0981b4d54p10c3d3jsn56c344ec88fa";
    String RAPID_API_HOST = "imdb-top-100-movies.p.rapidapi.com";

    @Headers({
            "X-RapidAPI-Key: " + RAPID_API_KEY,
            "X-RapidAPI-Host: " + RAPID_API_HOST
    })
    @GET("/")
    Call<List<MovieModels>> getMovie();

    @Headers({
            "X-RapidAPI-Key: " + RAPID_API_KEY,
            "X-RapidAPI-Host: " + RAPID_API_HOST
    })
    @GET("/{id}")
    Call<DetailModel> getMovieById(@Path("id") String movieId);

//    @Headers({
//            "X-RapidAPI-Key: " + RAPID_API_KEY,
//            "X-RapidAPI-Host: " + RAPID_API_HOST
//    })
//    @GET("/search")
//    Call<List<MovieModels>> searchMovies(@Query("query") String query);
}
