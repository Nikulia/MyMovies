package com.example.mymovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final int COUNT_FILMS_TO_END_WHERE_START_LOAD = 5;
    private List<Movie> movies;
    private OnPosterClickListener onPosterClickListener;
    private OnReachEndListener onReachEndListener;

    public MovieAdapter() {
        this.movies = new ArrayList<>();
    }

    public interface OnPosterClickListener {
        void onPosterClick(int position);
    }

    public interface OnReachEndListener {
        void onReachEnd();
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (isReachEnd(position) && onReachEndListener != null)
        onReachEndListener.onReachEnd();
        Movie movie = movies.get(position);
        Picasso.get().load(movie.getSmallPosterPath()).into(holder.imageViewSmallPoster);
    }

    private boolean isReachEnd(int position) {
        return movies.size() >= JsonUtils.COUNT_MOVIES_ON_PAGE &&
                position > movies.size() - COUNT_FILMS_TO_END_WHERE_START_LOAD;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewSmallPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPosterClickListener != null)
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                }
            });
        }

    }

    public void setMovies (List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void addMovie (Movie movie) {
        movies.add(movie);
        notifyDataSetChanged();
    }

    public void clear () {
        movies.clear();
        notifyDataSetChanged();
    }

    public void addMoviesToList (List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }
}
