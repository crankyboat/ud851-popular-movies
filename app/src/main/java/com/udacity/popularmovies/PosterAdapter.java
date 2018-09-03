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

    private List<Movie> mMovies;
    private ItemViewOnClickListener mItemViewOnClickListener;

    public PosterAdapter(List<Movie> movies, ItemViewOnClickListener listener) {
        this.mMovies = movies;
        this.mItemViewOnClickListener = listener;
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
        return mMovies.size();
    }

    public interface ItemViewOnClickListener {
        void onItemViewClick(int position);
    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context mContext;
        private ImageView mPosterImageView;

        public PosterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContext = itemView.getContext();
            mPosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
        }

        void bind(int position) {
            String posterImageUrl = mMovies.get(position).getPosterImageUrl();
            Picasso.with(mContext)
                    .load(posterImageUrl)
                    .placeholder(R.drawable.placeholder_poster)
                    .into(mPosterImageView);
        }

        @Override
        public void onClick(View itemView) {
            mItemViewOnClickListener.onItemViewClick(getAdapterPosition());
        }

    }

}
