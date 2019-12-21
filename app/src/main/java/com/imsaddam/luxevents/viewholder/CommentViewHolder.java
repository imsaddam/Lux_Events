package com.imsaddam.luxevents.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.models.ClickListener;


public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView name, comment, commentDate;
    public ImageView profilePicture;

    public CommentViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.rTitleTv);
        comment = (TextView) itemView.findViewById(R.id.userComment);
        commentDate = (TextView) itemView.findViewById(R.id.commentDate);
        profilePicture = (ImageView) itemView.findViewById(R.id.profile_Image);
    }

    private ClickListener mClickListener;


    public void setOnClickListener(ClickListener clickListener){
        mClickListener = clickListener;
    }
}