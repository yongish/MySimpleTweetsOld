package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.User;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivUser;
        public TextView tvName;
        public TextView tvUserScreenName;
        public TextView tvUserDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            ivUser = (ImageView) itemView.findViewById(R.id.ivUser);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvUserScreenName = (TextView) itemView.findViewById(R.id.tvUserScreenName);
            tvUserDescription = (TextView) itemView.findViewById(R.id.tvUserDescription);
        }
    }

    private List<User> mUsers;
    private Context mContext;

    public FriendsAdapter(Context context, List<User> users) {
        mUsers = users;
        mContext = context;
    }

    private Context getContext() { return mContext; }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View friendView = inflater.inflate(R.layout.item_user, parent, false);

        ViewHolder viewHolder = new ViewHolder(friendView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);

        ImageView ivUser = holder.ivUser;
        TextView tvName = holder.tvName;
        TextView tvUserScreenName = holder.tvUserScreenName;
        TextView tvUserDescription = holder.tvUserDescription;

        Glide.with(getContext()).load(user.getProfileImageUrl()).into(ivUser);
        tvName.setText(user.getName());
        tvUserScreenName.setText(user.getScreenName());
        tvUserDescription.setText(user.getTagline());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

}
