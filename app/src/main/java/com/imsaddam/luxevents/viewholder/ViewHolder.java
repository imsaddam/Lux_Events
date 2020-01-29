package com.imsaddam.luxevents.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.models.ClickListener;


public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView title, description, eventDate, eventLocation, EventUser;
    public ImageView eventImage;

    public ViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        title = (TextView) itemView.findViewById(R.id.rTitleTv);
        // description = (TextView) itemView.findViewById(R.id.rDescriptionTv);
        // eventLocation = (TextView) itemView.findViewById(R.id.eventLocation);
        eventDate = (TextView) itemView.findViewById(R.id.eventDate);
        eventImage = (ImageView) itemView.findViewById(R.id.rImageView);
        EventUser = itemView.findViewById(R.id.event_created_By);
    }

    private ClickListener mClickListener;


    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }
}