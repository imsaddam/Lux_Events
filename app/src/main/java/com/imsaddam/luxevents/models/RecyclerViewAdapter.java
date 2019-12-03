package com.imsaddam.luxevents.models;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.ui.createEvent.ViewEventFragment;
import com.imsaddam.luxevents.utils.DateHelper;
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
               // gotoViewEventFragment(events.get(position));
            }
        });


        return viewHolder;
    }

    private void gotoViewEventFragment(Event event){
        Fragment fragment = ViewEventFragment.newInstance(event);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Event event = events.get(position);
        try{
            Picasso.get().load(event.getImage()).into(holder.eventImage);
        }catch (Exception ex)
        {
            holder.eventImage.setImageDrawable(this.context.getResources().getDrawable(R.drawable.evetn_manager_icon,null));
            Log.d("Error","Invalid Image");
        }

        holder.title.setText(event.getTitle() == null ? "" : event.getTitle());
       // holder.description.setText(event.getDescription() == null ? "" : event.getDescription());
       // holder.eventLocation.setText(event.getLocation() == null ? "" : event.getLocation());
        holder.eventDate.setText(event.getEventDate() == null ? "" : DateHelper.dateToString(event.getEventDate()));
    }

    @Override
    public int getItemCount() {

        return events.size();
    }



}