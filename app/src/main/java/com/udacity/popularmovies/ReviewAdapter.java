package com.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies.model.MovieReview;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private static final String TAG = ReviewAdapter.class.getSimpleName();

    private List<MovieReview> mMovieReviews;

    public ReviewAdapter(List<MovieReview> movieReviews) {
        this.mMovieReviews = movieReviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(R.layout.item_review, parent, attachToParentImmediately);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovieReviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private TextView mReviewAuthorTextView;
        private TextView mReviewContentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mReviewAuthorTextView = (TextView) itemView.findViewById(R.id.tv_list_item_review_author);
            mReviewContentTextView = (TextView) itemView.findViewById(R.id.tv_list_item_review_content);
        }

        void bind(int position) {
            MovieReview movieReview = mMovieReviews.get(position);
            mReviewAuthorTextView.setText(movieReview.getAuthor());
            mReviewContentTextView.setText(movieReview.getContent());
        }

    }
}
