package com.imsaddam.luxevents.ui.createEvent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imsaddam.luxevents.MainActivity;
import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.models.Event;
import com.imsaddam.luxevents.utils.ToastHelper;
import com.shivtechs.maplocationpicker.MapUtility;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class EditEventActivity extends AppCompatActivity {

    private static final int LOC_REQ_CODE = 1;
    private static final int ADDRESS_PICKER_REQUEST = 1020;
    private final int PICK_IMAGE_REQUEST = 71;


    private Uri filePath;
    private ImageView imageView;
    EditText eventNameInput, eventDescriptionInput, saveVenue;
    DatePicker eventDate;
    TimePicker eventTime;
    Button eventSaveButton;
    Button btnChoose, selectPlaceBtn;
    Spinner spinner;
    // EventLocation location;

    private Event event;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    private static final String EVENT = "event";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_event);


        //get parametter from incoming intent
        Bundle b = getIntent().getBundleExtra("viewEvent");
        event = b.getParcelable("event");

        spinner = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.event_category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        eventNameInput = findViewById(R.id.text_create_event);
        eventDescriptionInput = findViewById(R.id.discription);

        eventSaveButton = findViewById(R.id.saveBtn);
        eventSaveButton.setText("Update Event");
        eventSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEvent();
            }
        });


        btnChoose = (Button) findViewById(R.id.btnChoose);
        imageView = (ImageView) findViewById(R.id.eventImage);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        eventDate = (DatePicker) findViewById(R.id.eventDate);
        eventTime = (TimePicker) findViewById(R.id.eventTime);

        saveVenue = findViewById(R.id.create_venue);
        setValue();
    }


    @SuppressLint("MissingPermission")
   /* private void showPlacePicker() {
        Intent intent = new Intent(getContext(), LocationPickerActivity.class);
        intent.putExtra(MapUtility.ADDRESS, location.getAddress());
        intent.putExtra(MapUtility.LATITUDE, location.getLatitude());
        intent.putExtra(MapUtility.LONGITUDE, location.getLongitude());
        startActivityForResult(intent, ADDRESS_PICKER_REQUEST);

    }*/


    private void setValue() {
        eventNameInput.setText(event.getTitle());
        eventDescriptionInput.setText(event.getDescription());
        spinner.setSelection(event.getCategory());
        saveVenue.setText(event.getVenue());


        if (event.getEventDate() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(event.getEventDate());

            eventDate.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), null);
            eventTime.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            eventTime.setCurrentMinute(c.get(Calendar.MINUTE));

        }
        //location = event.getLocation();
        // eventLocation.setText(event.getLocation().getAddress());

        try {
            Picasso.get().load(event.getImage()).into(imageView);
        } catch (Exception ex) {
            Log.d("Error", "Invalid Image");
        }

    }


    // call the mobile gallary for image selection
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    // after image selection what to do with image in this stage
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == ADDRESS_PICKER_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    String address = data.getStringExtra(MapUtility.ADDRESS);
                    double currentLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    double currentLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    //eventLocation.setText(address);
                    //location = new EventLocation(address,currentLatitude,currentLongitude);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



    private void updateEvent() {


        if (eventNameInput.getText().toString().isEmpty() ||
                eventDescriptionInput.getText().toString().isEmpty() ||
                spinner.getSelectedItemPosition() < 0 ||
                saveVenue.getText().toString().isEmpty()) {
            ToastHelper.showRedToast(getApplicationContext(), "Please fill all the required field.");
            return;
        }

        if (MainActivity.firebaseUser != null) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference ref = database.getReference("users/" + MainActivity.firebaseUser.getUid() + "/events/" + event.getKey());

            Calendar calendar = new GregorianCalendar(eventDate.getYear(),
                    eventDate.getMonth(),
                    eventDate.getDayOfMonth(),
                    eventTime.getCurrentHour(),
                    eventTime.getCurrentMinute());

            event.setTitle(eventNameInput.getText().toString());
            event.setDescription(eventDescriptionInput.getText().toString());
            event.setVenue(saveVenue.getText().toString());
            event.setCategory(spinner.getSelectedItemPosition());
            event.setImage(event.getImage() == null ? "No image" : event.getImage());
            //event.setLocation(location);
            event.setEventDate(calendar.getTime());

            ref.setValue(event)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (filePath != null) {
                                uploadImage(ref);
                            } else {
                                Toast.makeText(getApplicationContext(), "Event is updated.", Toast.LENGTH_SHORT).show();
                                gotoEventListFragment(event);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            Log.d("Ename", eventNameInput.getText().toString());
        }

    }


    //after update event its show the list of event
    private void gotoEventListFragment(Event event) {
        Intent i = new Intent(getApplicationContext(), ViewEventActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("event", event);
        i.putExtra("viewEvent", b);
        startActivity(i);
    }

    private void uploadImage(final DatabaseReference ref) {

        if (filePath != null) {
            storage = FirebaseStorage.getInstance("gs://lux-event.appspot.com");
            storageReference = storage.getReference();


            final StorageReference sRef = storageReference.child("images/" + ref.getKey());
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    ref.child("image").setValue(uri.toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    event.setImage(uri.toString());
                                                    Toast.makeText(getApplicationContext(), "Event is updated.",
                                                            Toast.LENGTH_SHORT).show();
                                                    gotoEventListFragment(event);

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
                            });

                            // progressDialog.dismiss();
                            //Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //  progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            // progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }


}
