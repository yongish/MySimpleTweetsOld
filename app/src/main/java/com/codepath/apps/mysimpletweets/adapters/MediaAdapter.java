package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Media;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivMedia;

        public ViewHolder (View itemView) {
            super(itemView);

            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);
        }
    }

    private List<Media> mMedia;
    private Context mContext;

    public MediaAdapter(Context context, List<Media> medias) {
        mMedia = medias;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View mediaView = inflater.inflate(R.layout.item_media, parent, false);

        ViewHolder viewHolder = new ViewHolder(mediaView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder (MediaAdapter.ViewHolder viewHolder, int position) {
        Media media = mMedia.get(position);

        ImageView ivMedia = viewHolder.ivMedia;
        Glide.with(getContext()).load(media.mediaUrl).into(ivMedia);
    }

    @Override
    public int getItemCount() {
        return mMedia.size();
    }
}
