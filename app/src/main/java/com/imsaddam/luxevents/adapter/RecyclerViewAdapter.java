package com.imsaddam.luxevents.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.models.ClickListener;
import com.imsaddam.luxevents.models.Event;
import com.imsaddam.luxevents.ui.createEvent.ViewEventActivity;
import com.imsaddam.luxevents.utils.DateHelper;
import com.imsaddam.luxevents.viewholder.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    Context context;
    FragmentActivity activity;
    List<Event> events;

    public RecyclerViewAdapter(Context context, FragmentActivity activity, List<Event> TempList) {

        this.events = TempList;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.setOnClickListener(new ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                gotoViewEventFragment(events.get(position));
            }
        });


        // its reperents the individual row in the list from adapter view
        return viewHolder;
    }

    // Its shows the individual event details

    private void gotoViewEventFragment(Event event) {
        Intent i = new Intent(context, ViewEventActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("event", event);
        i.putExtra("viewEvent", b);
        context.startActivity(i);
    }


    // render only one event at a time from the full list
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Event event = events.get(position);
        try {
            Picasso.get().load(event.getImage()).into(holder.eventImage);
        } catch (Exception ex) {
            holder.eventImage.setImageDrawable(this.context.getResources().getDrawable(R.drawable.evetn_manager_icon, null));
            Log.d("Error", "Invalid Image");
        }

        holder.title.setText(event.getTitle() == null ? "" : event.getTitle());
        // holder.description.setText(event.getDescription() == null ? "" : event.getDescription());
        // holder.eventLocation.setText(event.getLocation() == null ? "" : event.getLocation());
        holder.eventDate.setText(event.getEventDate() == null ? "" : DateHelper.dateToString(event.getEventDate()));
        holder.EventUser.setText(event.getEventAddedBy().getName());
    }

    @Override
    public int getItemCount() {

        return events.size();
    }


}