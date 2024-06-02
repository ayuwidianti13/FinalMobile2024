package com.example.infomovie.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infomovie.Adapter.MovieAdapter;
import com.example.infomovie.Api.ApiConfig;
import com.example.infomovie.Api.ApiServices;
import com.example.infomovie.Model.MovieModels;
import com.example.infomovie.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {
    private ApiServices apiServices;
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<MovieModels> movieModels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        context = getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        apiServices = ApiConfig.getClient().create(ApiServices.class);

        movieAdapter = new MovieAdapter(movieModels, context);
        recyclerView.setAdapter(movieAdapter);

        fetchDataFromApi();

        return view;
    }

    private void fetchDataFromApi() {
        Call<List<MovieModels>> call = apiServices.getMovie();
        call.enqueue(new Callback<List<MovieModels>>() {
            @Override
            public void onResponse(Call<List<MovieModels>> call, Response<List<MovieModels>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    movieModels.clear(); // Clear old data
                    movieModels.addAll(response.body());
                    movieAdapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<MovieModels>> call, Throwable t) {

            }
        });
    }
}
