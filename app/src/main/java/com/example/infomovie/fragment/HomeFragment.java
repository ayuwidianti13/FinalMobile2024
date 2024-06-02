package com.example.infomovie.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.example.infomovie.Sqlite.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private ApiServices apiServices;
    private MovieAdapter movieAdapter;
    private MovieAdapter searchAdapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSearchResults;
    private SearchView searchView;
    private TextView tvNoData;
    private TextView tvHalloNama;
    private Context context;
    private ArrayList<MovieModels> movieModels = new ArrayList<>();
    private ArrayList<MovieModels> searchResults = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private ProgressBar progressBar;
    private ProgressBar progressBarSearch;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewSearchResults = view.findViewById(R.id.recyclerViewSearchResults);
        searchView = view.findViewById(R.id.search_movie);
        tvNoData = view.findViewById(R.id.tvNoData);
        tvHalloNama = view.findViewById(R.id.tv_halloNama);
        progressBar = view.findViewById(R.id.progressBar);
        progressBarSearch = view.findViewById(R.id.progressBarSearch);
        context = getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(context));

        apiServices = ApiConfig.getClient().create(ApiServices.class);

        movieAdapter = new MovieAdapter(movieModels, context);
        searchAdapter = new MovieAdapter(searchResults, context);
        recyclerView.setAdapter(movieAdapter);
        recyclerViewSearchResults.setAdapter(searchAdapter);

        databaseHelper = new DatabaseHelper(context);

        // Mengambil dan menampilkan username terbaru
        displayUsername();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    recyclerView.setVisibility(View.GONE);
                    recyclerViewSearchResults.setVisibility(View.VISIBLE);
                    searchResults.clear();
                    searchAdapter.notifyDataSetChanged();
                    tvNoData.setVisibility(View.GONE);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchTask().execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerViewSearchResults.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.GONE);
                } else {
                    new SearchTask().execute(newText);
                }
                return false;
            }
        });

        fetchDataFromApi();

        return view;
    }

    private void fetchDataFromApi() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);
        Call<List<MovieModels>> call = apiServices.getMovie(); //mmbuat objek melakukan panggilan API untuk mendapatkan daftar film
        call.enqueue(new Callback<List<MovieModels>>() { //Menjalankan panggilan API
            @Override
            public void onResponse(Call<List<MovieModels>> call, Response<List<MovieModels>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    movieModels.clear();
                    movieModels.addAll(response.body());
                    movieAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MovieModels>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class SearchTask extends AsyncTask<String, Void, List<MovieModels>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarSearch.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieModels> doInBackground(String... strings) {
            String query = strings[0];
            ArrayList<MovieModels> filteredList = new ArrayList<>();
            for (MovieModels movie : movieModels) {
                if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(movie);
                }
            }
            return filteredList;
        }

        @Override
        protected void onPostExecute(List<MovieModels> filteredList) {
            super.onPostExecute(filteredList);
            progressBarSearch.setVisibility(View.GONE);

            if (!filteredList.isEmpty()) {
                searchResults.clear();
                searchResults.addAll(filteredList);
                searchAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.GONE);
                recyclerViewSearchResults.setVisibility(View.VISIBLE);
                tvNoData.setVisibility(View.GONE);
            } else {
                searchResults.clear();
                searchAdapter.notifyDataSetChanged();
                recyclerViewSearchResults.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    private void displayUsername() {
        String username = databaseHelper.getLoggedInUsername();
        if (username != null) {
            tvHalloNama.setText(username);
        }
    }
}
