package com.imsaddam.luxevents.ui.createEvent;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.imsaddam.luxevents.MainActivity;
import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.adapter.CommentRecyclerViewAdapter;
import com.imsaddam.luxevents.models.Comment;
import com.imsaddam.luxevents.models.Event;
import com.imsaddam.luxevents.models.User;
import com.imsaddam.luxevents.utils.DateHelper;
import com.imsaddam.luxevents.utils.ToastHelper;
import com.squareup.picasso.Picasso;


import java.util.List;


public class ViewEventActivity extends AppCompatActivity {

    public static final String EVENT_KEY = "event";
    private TextView title;
    private TextView description;
    private TextView time;
    private TextView venue;
    private TextView category;
    private Event event;
    public ImageView image;
    Button commentButton;
    EditText commentText;
    RecyclerView mRecyclerView;
    private TextView numGoing;
    RecyclerView.Adapter adapter;
    ImageButton like;
    private int descriptionLineCount = 0;
    User user;

    private boolean isLiked = false;

    FloatingActionButton eventEdit;

    private OnFragmentInteractionListener mListener;

    public ViewEventActivity() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_event);
        Bundle b = getIntent().getBundleExtra("viewEvent");
        event = b.getParcelable("event");

        findViews();
        setValue();

        commentButton = findViewById(R.id.commentButton);
        commentText = findViewById(R.id.commentText);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comment();

            }
        });


        like = findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                likeEvent();

            }
        });

        mRecyclerView = findViewById(R.id.commentRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        //set layout as LinearLayout
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        adapter = new CommentRecyclerViewAdapter(getApplicationContext(), ViewEventActivity.this, event.getComments());
        mRecyclerView.setAdapter(adapter);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users/" + MainActivity.firebaseUser.getUid());

        // Attach a listener to read the data at our posts reference
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void findViews() {
        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        time = findViewById(R.id.time);
        numGoing = findViewById(R.id.numGoing);
        //organization = findViewById(R.id.organization);
        venue = findViewById(R.id.view_venue);
        //more = findViewById(R.id.more);
        // moreButtonGradient = findViewById(R.id.moreButtonGradient);
        eventEdit = findViewById(R.id.eventEdit);

        eventEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EditEventActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("event", event);
                i.putExtra("viewEvent", b);
                startActivity(i);
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent back = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(back);
            }
        });
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareCompat.IntentBuilder.from(ViewEventActivity.this)
                        .setType("text/plain")
                        .setChooserTitle("Choose how to share this event")
                        .setText("Event:" + event.getTitle() + "\nDescription: " + event.getDescription() + "\nDate:" + event.getEventDate() + "\nLocation:" + event.getLocation())
                        .setStream(Uri.parse(event.getImage() == null ? "" : event.getImage()))
                        .startChooser();
            }
        });

        // scrollView = findViewById(R.id.scrollView);

        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        image.setPadding(0, titleBarHeight, 0, 0);

    }

    private void likeEvent() {
        if (MainActivity.firebaseUser != null) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference ref = database.getReference("users/" + event.getEventAddedBy().getKey() + "/events/" + event.getKey());

            final List<String> likeId = event.getLikedId();

            if (likeId.contains(MainActivity.firebaseUser.getUid())) {
                likeId.remove(MainActivity.firebaseUser.getUid());
                isLiked = false;
            } else {
                likeId.add(MainActivity.firebaseUser.getUid());
                isLiked = true;
            }

            ref.child("likes").setValue(likeId)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            if (isLiked) {
                                like.setImageDrawable(getResources().getDrawable(R.drawable.likeactive, null));
                                Toast.makeText(getApplicationContext(), "Liked.", Toast.LENGTH_SHORT).show();
                            } else {
                                like.setImageDrawable(getResources().getDrawable(R.drawable.like, null));
                                Toast.makeText(getApplicationContext(), "Disliked.", Toast.LENGTH_SHORT).show();
                            }
                            event.setLikedId(likeId);
                            numGoing.setText(event.getLikedId().size() + " Liked");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


    private void setValue() {
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        category.setText(getResources().getStringArray(R.array.event_category_array)[event.getCategory()]);
        time.setText(DateHelper.dateToString(event.getEventDate()));
        numGoing.setText(2 + " Liked");
        // organization.setText("Admin");
        venue.setText(event.getVenue());
        //location.setText(event.getLocation().getAddress());
        //configureDescription();
        //setMap(event.getLocation().getLatitude(), event.getLocation().getLongitude());
        try {
            Picasso.get().load(event.getImage()).into(image);
        } catch (Exception ex) {
            Log.d("Error", "Invalid Image");
        }
    }


    private void comment() {

        if (commentText.getText().toString().isEmpty()) {
            ToastHelper.showRedToast(getApplicationContext(), "Please add your comment.");
            return;
        }

        if (MainActivity.firebaseUser != null) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference ref = database.getReference("users/" + event.getEventAddedBy().getKey() + "/events/" + event.getKey());
            final List<Comment> comments = event.getComments();

            comments.add(new Comment(user, commentText.getText().toString()));

            ref.child("comments").setValue(comments)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            commentText.setText("");
                            Toast.makeText(getApplicationContext(), "Comment is added.", Toast.LENGTH_SHORT).show();
                            event.setComments(comments);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
