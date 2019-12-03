package com.imsaddam.luxevents.ui.createEvent;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imsaddam.luxevents.MainActivity;
import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.models.Event;
import com.imsaddam.luxevents.models.RecyclerViewAdapter;
import com.imsaddam.luxevents.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class EventsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "isAllEvents";

    // TODO: Rename and change types of parameters
    private boolean isAllEvents;

    private OnFragmentInteractionListener mListener;

    LinearLayoutManager mLayoutManager; //for sorting
    SharedPreferences mSharedPref; //for saving sort settings
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    //DatabaseReference mRef;
    Query mRef;
    List<Event> events = new ArrayList<>();

    RecyclerView.Adapter adapter ;

    private boolean mSearchCheck;


    public EventsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isAllEvents Parameter 1.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsListFragment newInstance(boolean isAllEvents) {
        EventsListFragment fragment = new EventsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isAllEvents);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAllEvents = getArguments().getBoolean(ARG_PARAM1);
        }



        mSharedPref = getActivity().getSharedPreferences("SortSettings", Context.MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "newest"); //where if no settingsis selected newest will be default

        if (mSorting.equals("newest")) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            //this will load the items from bottom means newest first
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        } else if (mSorting.equals("oldest")) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            //this will load the items from bottom means oldest first
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Event List");

        // Inflate the layout for this fragment
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);

        //RecyclerView
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        //set layout as LinearLayout
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //send Query to FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        if(isAllEvents)
        {
            mRef = mFirebaseDatabase.getReference("users");
            //mRef = mFirebaseDatabase.getReference("users/"+ MainActivity.firebaseUser.getUid()+"/events");

        }else{
            mRef = mFirebaseDatabase.getReference("users").orderByKey().equalTo(MainActivity.firebaseUser.getUid());
        }

        events= new ArrayList<>();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                events.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    Map<String, Object> objectMap = (HashMap<String, Object>)
                            postSnapshot.getValue();

                    if(objectMap.containsKey("events"))
                    {
                        User user = postSnapshot.getValue(User.class);
                        user.setKey(postSnapshot.getKey());
                        for (DataSnapshot eventSnap: postSnapshot.child("events").getChildren()) {
                            Event event = eventSnap.getValue(Event.class);
                            event.setKey(eventSnap.getKey());
                            event.setEventAddedBy(user);
                            events.add(event);
                        }
                    }
                    // here you can access to name property like university.name
                }

                Collections.sort(events, new Comparator<Event>() {
                    public int compare(Event o1, Event o2) {
                        if (o1.getEventDate() == null || o2.getEventDate() == null)
                            return 0;
                        return o1.getEventDate().compareTo(o2.getEventDate());
                    }
                });

                //events.sort((o1,o2) -> o1.getEventDate() == null ? 0 : o2.getEventDate() ==null ? 0 :  o1.getEventDate().compareTo(o2.getEventDate()));

                adapter = new RecyclerViewAdapter(getContext(),getActivity(), events);
                mRecyclerView.setAdapter(adapter);

//                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    //search data
    private void firebaseSearch(String searchText) {
        List<Event> eventList = new ArrayList<>();
        for (Event event :events) {
            if(event.getTitle() != null && event.getTitle().toLowerCase().contains(searchText.toLowerCase()))
            {
                eventList.add(event);
            }
        }
        adapter = new RecyclerViewAdapter(getContext(),getActivity(), eventList );
        mRecyclerView.setAdapter(adapter);
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }


    private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            firebaseSearch(query == null ? "" : query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            //Filter as you type
            firebaseSearch(newText == null ? "" : newText);
            return false;
        }
    };




}
