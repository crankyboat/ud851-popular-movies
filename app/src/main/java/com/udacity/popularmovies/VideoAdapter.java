package com.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies.model.MovieVideo;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{

    private static final String TAG = VideoAdapter.class.getSimpleName();

    private List<MovieVideo> mMovieVideos;
    private ItemViewOnClickListener mItemViewOnClickListener;

    public VideoAdapter(List<MovieVideo> movieVideos, ItemViewOnClickListener listener) {
        this.mMovieVideos = movieVideos;
        this.mItemViewOnClickListener = listener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(R.layout.item_video, parent, attachToParentImmediately);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovieVideos.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context mContext;
        private TextView mVideoNameTextView;
        private TextView mVideoTypeTextView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContext = itemView.getContext();
            mVideoNameTextView = (TextView) itemView.findViewById(R.id.tv_list_item_video_name);
            mVideoTypeTextView = (TextView) itemView.findViewById(R.id.tv_list_item_video_type);
        }

        void bind(int position) {
            MovieVideo movieVideo = mMovieVideos.get(position);
            mVideoNameTextView.setText(movieVideo.getName());
            mVideoTypeTextView.setText(movieVideo.getType());
        }

        @Override
        public void onClick(View itemView) {
            mItemViewOnClickListener.onItemViewClick(getAdapterPosition());
        }

    }
}
