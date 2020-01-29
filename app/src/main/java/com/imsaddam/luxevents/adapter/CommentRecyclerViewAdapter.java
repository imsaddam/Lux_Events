package com.imsaddam.luxevents.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.models.Comment;
import com.imsaddam.luxevents.models.User;
import com.imsaddam.luxevents.utils.DateHelper;
import com.imsaddam.luxevents.viewholder.CommentViewHolder;
import com.squareup.picasso.Picasso;


import java.util.List;


public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    Context context;
    FragmentActivity activity;
    List<Comment> comments;

    public CommentRecyclerViewAdapter(Context context, FragmentActivity activity, List<Comment> TempList) {
        this.comments = TempList;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_comment_row_new, parent, false);

        CommentViewHolder viewHolder = new CommentViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        User user = comment.getUser();
        holder.name.setText(user.getName() == null ? "" : user.getName());
        holder.comment.setText(comment.getComments() == null ? "" : comment.getComments());
        holder.commentDate.setText(comment.getAddedDate() == null ? "" : DateHelper.dateToString(comment.getAddedDate()));

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}