package com.example.talk.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.talk.Modals.Comment;
import com.example.talk.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;


    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment, viewGroup, false);

        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        Glide.with(mContext).load(mData.get(i).getUserImageUrl()).into(commentViewHolder.userImage);
        commentViewHolder.userName.setText(mData.get(i).getUserName());
        commentViewHolder.commentText.setText(mData.get(i).getContent());
        commentViewHolder.commentDate.setText(timestampToString((Long) mData.get(i).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName, commentText, commentDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.row_comment_userphoto);
            userName = itemView.findViewById(R.id.row_comment_username);
            commentText = itemView.findViewById(R.id.row_comment_text);
            commentDate = itemView.findViewById(R.id.row_comment_date);
        }
    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm", calendar).toString();
        return date;
    }

}
