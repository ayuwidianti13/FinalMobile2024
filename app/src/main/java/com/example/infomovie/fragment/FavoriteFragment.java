package com.example.infomovie.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.infomovie.Adapter.FavoriteAdapter;
import com.example.infomovie.Api.ApiConfig;
import com.example.infomovie.Api.ApiServices;
import com.example.infomovie.Model.MovieModels;
import com.example.infomovie.R;
import com.example.infomovie.Sqlite.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter favoritAdapter;
    private DatabaseHelper databaseHelper;
    private ApiServices service;
    private SharedPreferences preferences;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi RecyclerView dan ProgressBar
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progressBar);

        service = ApiConfig.getClient().create(ApiServices.class);
        databaseHelper = new DatabaseHelper(requireActivity());
        preferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        displayFavoriteMovies();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayFavoriteMovies();
    }

    private void displayFavoriteMovies() {
        int userId = preferences.getInt("user_id", 0);

        // Ambil daftar film favorit dari database
        Cursor cursor = databaseHelper.getFavoriteMoviesByUserId(userId);
        ArrayList<String> favoritesMoviesId = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String movieId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOVIE_ID));
                favoritesMoviesId.add(movieId);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Sembunyikan RecyclerView dan tampilkan ProgressBar saat memuat data
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Jalankan tugas background untuk memanggil API
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<List<MovieModels>> call = service.getMovie();
            call.enqueue(new Callback<List<MovieModels>>() {
                @Override
                public void onResponse(@NonNull Call<List<MovieModels>> call, @NonNull Response<List<MovieModels>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<MovieModels> movieModels = response.body();
                        List<MovieModels> favoriteMovies = new ArrayList<>(); // untuk menyimpan film-film yang merupakan favorit pengguna.
                        for (MovieModels movieModel : movieModels) {
                            if (favoritesMoviesId.contains(movieModel.getId())) {
                                favoriteMovies.add(movieModel);
                            }
                        }
                        favoritAdapter = new FavoriteAdapter(getParentFragmentManager(), favoriteMovies, userId);
                        recyclerView.setAdapter(favoritAdapter);

                        handler.post(() -> {
                            progressBar.setVisibility(View.GONE);
                            if (favoriteMovies.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        handler.post(() -> progressBar.setVisibility(View.GONE));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<MovieModels>> call, @NonNull Throwable t) {
                    handler.post(() -> progressBar.setVisibility(View.GONE));
                }
            });
        });
    }
}
