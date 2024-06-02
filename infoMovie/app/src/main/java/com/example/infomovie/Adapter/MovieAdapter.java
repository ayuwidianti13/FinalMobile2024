package com.example.infomovie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infomovie.Model.MovieModels;
import com.example.infomovie.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<MovieModels> movieModels;
    private Context context;

    public MovieAdapter(List<MovieModels> movieModels, Context context) {
        this.movieModels = movieModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        holder.bind(movieModels.get(position));
    }

    @Override
    public int getItemCount() {
        return movieModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_photo;
        TextView tv_title;
        RatingBar rb_rating;
        TextView i_year;

        public ViewHolder(View view) {
            super(view);
            iv_photo = view.findViewById(R.id.imgPhoto);
            tv_title = view.findViewById(R.id.tvTitle);
            rb_rating = view.findViewById(R.id.ratingBar);
            i_year = view.findViewById(R.id.tvTahun);
        }

        public void bind(MovieModels movieModels) {
            // Load image using Picasso library
            Picasso.get().load(movieModels.getBig_image()).into(iv_photo);

            // Set title text
            tv_title.setText(movieModels.getTitle());

            // Set rating
            float rating = Float.parseFloat(movieModels.getRating());
            rb_rating.setRating(rating);

            // Set year
            i_year.setText(String.valueOf(movieModels.getYear()));
        }
    }
}
