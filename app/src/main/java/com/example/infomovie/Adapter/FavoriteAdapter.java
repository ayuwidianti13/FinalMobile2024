package com.example.infomovie.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infomovie.DetailActivity;
import com.example.infomovie.Model.MovieModels;
import com.example.infomovie.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private final FragmentManager fragmentManager;
    private List<MovieModels> movieModels;
    private Context context;
    private final int userId;

    public FavoriteAdapter(FragmentManager fragmentManager, List<MovieModels> movieModels, int userId) {
        this.fragmentManager = fragmentManager;
        this.movieModels = movieModels;
        this.userId = userId;
    }

    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        MovieModels movieModel = movieModels.get(position);
        holder.bind(movieModel);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("movieId", movieModel.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieModels.size();
    }

    public void updateData(List<MovieModels> newMovieModels) {
        this.movieModels.clear();
        this.movieModels.addAll(newMovieModels);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_photo;
        TextView tv_title;
        TextView rb_rating;
        TextView i_year;

        public ViewHolder(View view) {
            super(view);
            iv_photo = view.findViewById(R.id.imgPhoto);
            tv_title = view.findViewById(R.id.tvTitle);
            rb_rating = view.findViewById(R.id.ratingBar);
            i_year = view.findViewById(R.id.tvTahun);
        }

        public void bind(MovieModels movieModel) {
            Picasso.get().load(movieModel.getBig_image()).into(iv_photo);
            tv_title.setText(movieModel.getTitle());
            rb_rating.setText(movieModel.getRating());
            i_year.setText(String.valueOf(movieModel.getYear()));
        }
    }
}
