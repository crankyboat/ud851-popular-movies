package com.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.model.Movie;

import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder>{

    private static final String TAG = PosterAdapter.class.getSimpleName();

    private List<Movie> movies;
    private ItemViewOnClickListener itemViewOnClickListener;

    public interface ItemViewOnClickListener {
        void onItemViewClick(int position);
    }

    public PosterAdapter(List<Movie> movies, ItemViewOnClickListener listener) {
        this.movies = movies;
        this.itemViewOnClickListener = listener;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(R.layout.item_poster, parent, attachToParentImmediately);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context context;
        private ImageView posterImageView;

        public PosterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            posterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
        }

        void bind(int position) {
            String posterImageUrl = movies.get(position).getPosterImageUrl();
            Picasso.with(context)
                    .load(posterImageUrl)
                    .placeholder(R.drawable.placeholder_poster)
                    .into(posterImageView);
        }

        @Override
        public void onClick(View itemView) {
            itemViewOnClickListener.onItemViewClick(getAdapterPosition());
        }

    }

}
