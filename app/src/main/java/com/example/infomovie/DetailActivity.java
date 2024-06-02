package com.example.infomovie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infomovie.Api.ApiConfig;
import com.example.infomovie.Api.ApiServices;
import com.example.infomovie.Model.DetailModel;
import com.example.infomovie.R;
import com.example.infomovie.Sqlite.DatabaseHelper;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailActivity extends AppCompatActivity {

    private String movieId;
    private ImageView iv_photo;
    private ImageView iv_back;
    private ImageView iv_fav;
    private TextView tv_ratingdet;
    private TextView tv_yeardet;
    private TextView tv_titledet;
    private TextView tv_desc;
    private ProgressBar progressBar;
    private TextView textdesc;
    private ImageView imgstar;
    private boolean isFavorite = false;
    private DatabaseHelper databaseHelper;
    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView tvError;
    private Button btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        databaseHelper = new DatabaseHelper(this); // instance

        iv_photo = findViewById(R.id.iv_photo);
        iv_back = findViewById(R.id.iv_back);
        iv_fav = findViewById(R.id.iv_fav);
        tv_ratingdet = findViewById(R.id.tv_ratingdet);
        tv_yeardet = findViewById(R.id.tv_yeardet);
        tv_titledet = findViewById(R.id.tv_titledet);
        tv_desc = findViewById(R.id.tv_desc);
        progressBar = findViewById(R.id.progresbarDetail);
        textdesc = findViewById(R.id.textDesc);
        imgstar = findViewById(R.id.imgstar);
        tvError = findViewById(R.id.error);
        btnRetry = findViewById(R.id.retry);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("movieId")) {
            movieId = intent.getStringExtra("movieId");
            if (movieId != null) {
                getMovieDetails(movieId);
            } else {
                Toast.makeText(this, "Invalid Movie ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Movie ID not provided", Toast.LENGTH_SHORT).show();
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_fav.setOnClickListener(v -> {
            int loggedInUserId = getLoggedInUserId();
            if (!isFavorite) {
                saveFavoriteMovie(loggedInUserId, movieId);
                iv_fav.setImageResource(R.drawable.love);
                isFavorite = true;
                iv_fav.setEnabled(false); // Disable button to prevent multiple clicks
                Toast.makeText(DetailActivity.this, "Film added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                removeFavoriteMovie(loggedInUserId, movieId);
                iv_fav.setImageResource(R.drawable.baseline_favorite_border_24);
                isFavorite = false;
                iv_fav.setEnabled(true); // Enable button to allow clicking
                Toast.makeText(DetailActivity.this, "Film removed from favorites", Toast.LENGTH_SHORT).show();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovieDetails(movieId);
            }
        });
    }

    private void getMovieDetails(String movieId) {
        Retrofit retrofit = ApiConfig.getClient(); // untuk membuat panggilan ke API.
        ApiServices apiServices = retrofit.create(ApiServices.class);
        progressBar.setVisibility(View.VISIBLE);

        // Sembunyikan elemen lain saat ProgressBar muncul
        hideViews();

        Call<DetailModel> call = apiServices.getMovieById(movieId);
        call.enqueue(new Callback<DetailModel>() {
            @Override
            public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                handler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        DetailModel detailModel = response.body();

                        tv_titledet.setText(detailModel.getTitle());
                        tv_yeardet.setText(String.valueOf(detailModel.getYear()));
                        tv_ratingdet.setText(detailModel.getRating());
                        tv_desc.setText(detailModel.getDescription());
                        Picasso.get().load(detailModel.getBig_image()).into(iv_photo);

                        showViews();

                        int loggedInUserId = getLoggedInUserId();
                        isFavorite = databaseHelper.isFavorite(loggedInUserId, movieId); // Memeriksa apakah film dengan movieId tertentu sudah ditandai sebagai favorit
                        iv_fav.setImageResource(isFavorite ? R.drawable.love : R.drawable.baseline_favorite_border_24);
                        iv_fav.setEnabled(true);

                        // Sembunyikan pesan error dan tombol retry setelah data berhasil diambil
                        showErrorMessage(false);

                    } else {
                        showErrorMessage(true);
                        Toast.makeText(DetailActivity.this, "Failed to retrieve movie details", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }

            // ketika pemanggilan ke API gagal
            @Override
            public void onFailure(Call<DetailModel> call, Throwable t) {
                handler.post(() -> {
                    showErrorMessage(true);
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private int getLoggedInUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1);
    }

    private void saveFavoriteMovie(int userId, String movieId) {
        databaseHelper.insertFavorite(userId, movieId);
    }

    private void removeFavoriteMovie(int userId, String movieId) {
        databaseHelper.deleteFavorite(userId, movieId);
    }

    private void hideViews() {
        iv_photo.setVisibility(View.GONE);
        iv_back.setVisibility(View.GONE);
        iv_fav.setVisibility(View.GONE);
        tv_ratingdet.setVisibility(View.GONE);
        tv_yeardet.setVisibility(View.GONE);
        tv_titledet.setVisibility(View.GONE);
        tv_desc.setVisibility(View.GONE);
        textdesc.setVisibility(View.GONE);
        imgstar.setVisibility(View.GONE);
    }

    private void showViews() {
        iv_photo.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        iv_fav.setVisibility(View.VISIBLE);
        tv_ratingdet.setVisibility(View.VISIBLE);
        tv_yeardet.setVisibility(View.VISIBLE);
        tv_titledet.setVisibility(View.VISIBLE);
        tv_desc.setVisibility(View.VISIBLE);
        textdesc.setVisibility(View.VISIBLE);
        imgstar.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(boolean show) {
        if (show) {
            tvError.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.VISIBLE);
        } else {
            tvError.setVisibility(View.GONE);
            btnRetry.setVisibility(View.GONE);
        }
    }
}
