package com.imsaddam.luxevents.ui.createEvent;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ShareCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.imsaddam.luxevents.MainActivity;
import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.models.Event;
import com.imsaddam.luxevents.utils.DateHelper;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewEventFragment extends Fragment {

    public static final String EVENT_KEY = "event";
    private TextView title;
    private TextView description;
    private TextView more;
    private View moreButtonGradient;
    private TextView time;
    private TextView numGoing;
    private TextView organization;
    private TextView location;
    private TextView category;
    private Event event;
    public ImageView image;
    MapView mapView;
    private NestedScrollView scrollView;
    private int descriptionLineCount = 0;

    FloatingActionButton eventEdit;

    private OnFragmentInteractionListener mListener;

    public ViewEventFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ViewEventFragment newInstance(Event event) {
        ViewEventFragment fragment = new ViewEventFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("View Event");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_event, container, false);

        findViews(rootView);
        setValue();
        if(!MainActivity.firebaseUser.getUid().equals(event.getEventAddedBy().getKey()))
        {
            eventEdit.hide();
        }

        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ));
        return rootView;
    }

    private void findViews(View rootView)
    {
        image = rootView.findViewById(R.id.image);
        title = rootView.findViewById(R.id.title);
        description = rootView.findViewById(R.id.description);
        category = rootView.findViewById(R.id.category);
        time = rootView.findViewById(R.id.time);
        numGoing = rootView.findViewById(R.id.numGoing);
        organization = rootView.findViewById(R.id.organization);
        location = rootView.findViewById(R.id.location);
        more = rootView.findViewById(R.id.more);
        moreButtonGradient = rootView.findViewById(R.id.moreButtonGradient);
        eventEdit = rootView.findViewById(R.id.eventEdit);
        mapView = (MapView) rootView.findViewById(R.id.map_view);

        eventEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = EditEventFragment.newInstance(event);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ViewPagerFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        rootView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setChooserTitle("Choose how to share this event")
                        .setText("Event:"+ event.getTitle() +"\nDescription: "+ event.getDescription()+"\nDate:"+ event.getEventDate() +"\nLocation:"+event.getLocation())
                        .setStream(Uri.parse(event.getImage()==null?"": event.getImage()))
                        .startChooser();
            }
        });

        scrollView = rootView.findViewById(R.id.scrollView);

        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        image.setPadding(0, titleBarHeight, 0, 0);

    }


    private void setValue()
    {
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        category.setText(getResources().getStringArray(R.array.event_category_array)[event.getCategory()]);
        time.setText(DateHelper.dateToString(event.getEventDate()));
        numGoing.setText(2 + " Going");
        organization.setText(event.getEventAddedBy().getName());

        //location.setText(event.getLocation().getAddress());
        configureDescription();
        //setMap(event.getLocation().getLatitude(), event.getLocation().getLongitude());
        try{
            Picasso.get().load(event.getImage()).into(image);
        }catch (Exception ex)
        {
            Log.d("Error","Invalid Image");
        }
    }


    //this method is to show map
    /*public void setMap(final double latitude, final double longitude){

        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(
                new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googlemap) {
                        final GoogleMap map = googlemap;

                        MapsInitializer.initialize(getContext());
                        //change map type as your requirements
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        //user will see a blue dot in the map at his location
                        map.setMyLocationEnabled(true);
                        LatLng marker =new LatLng(latitude, longitude);

                        //move the camera default animation
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 8));

                        //add a default marker in the position
                        map.addMarker(new MarkerOptions().title(event.getLocation().getAddress())
                                .position(marker)).showInfoWindow();

                    }
                }
        );
    }
*/
    private void configureDescription()
    {
        //find out how many lines the description text will be
        description.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                if (more.getVisibility() != View.VISIBLE)
                    return true;
                // save description line count only first time
                int lineCount = description.getLayout().getLineCount();
                if (descriptionLineCount > lineCount)
                    return true;

                descriptionLineCount = lineCount;
                if (descriptionLineCount >= 5)
                {
                    description.setLines(5);
                    more.setVisibility(View.VISIBLE);
                    moreButtonGradient.setVisibility(View.VISIBLE);
                }
                else
                {
                    description.setLines(descriptionLineCount);
                    more.setVisibility(View.GONE);
                    moreButtonGradient.setVisibility(View.GONE);
                }
                return true;
            }
        });
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
