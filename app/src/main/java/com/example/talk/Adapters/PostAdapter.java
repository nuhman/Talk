package com.example.talk.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.talk.Modals.Post;
import com.example.talk.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>{

    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, viewGroup, false);

        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.title.setText(mData.get(i).getTitle());
        myViewHolder.author.setText(mData.get(i).getTitle());
        Glide.with(mContext).load(mData.get(i).getPic()).into(myViewHolder.bg);
        Glide.with(mContext).load(mData.get(i).getUserPhoto()).into(myViewHolder.profilePhoto);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title, author;
        ImageView bg, profilePhoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.row_post_title);
            author = itemView.findViewById(R.id.row_post_author);
            bg = itemView.findViewById(R.id.row_post_bg);
            profilePhoto = itemView.findViewById(R.id.row_post_profile_photo);
        }
    }
}
