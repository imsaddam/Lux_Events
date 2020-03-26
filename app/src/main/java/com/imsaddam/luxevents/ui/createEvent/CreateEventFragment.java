package com.imsaddam.luxevents.ui.createEvent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imsaddam.luxevents.MainActivity;
import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.models.Event;
import com.imsaddam.luxevents.models.EventLocation;
import com.imsaddam.luxevents.utils.ToastHelper;
import com.shivtechs.maplocationpicker.LocationPickerActivity;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreateEventFragment extends Fragment {

    private static final int LOC_REQ_CODE = 1;
    private static final int ADDRESS_PICKER_REQUEST = 1020;
    private final int PICK_IMAGE_REQUEST = 71;
    EditText eventNameInput, eventDescriptionInput, saveVenue;
    DatePicker eventDate;
    TimePicker eventTime;
    Button btnChoose, eventSaveButton;
    Spinner spinner;
    EventLocation location;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private ImageView imageView;
    private Place place;

    public static CreateEventFragment newInstance() {
        CreateEventFragment mFragment = new CreateEventFragment();
        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("New Event"); // Set a title for activity

        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false); // Call the XML layout for create event activity


        //  MapUtility.apiKey = getResources().getString(R.string.google_maps_key);

        spinner = rootView.findViewById(R.id.category_spinner); // Call the spinner category
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.event_category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //  eventLocation = (TextInputEditText) rootView.findViewById(R.id.eventLocation);


        eventNameInput = rootView.findViewById(R.id.text_create_event); // Call the event name view by id
        eventDescriptionInput = rootView.findViewById(R.id.discription); // Event description id call

        eventSaveButton = rootView.findViewById(R.id.saveBtn);  // Event save button
        saveVenue = rootView.findViewById(R.id.create_venue); // Event Venue name
        eventSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
            }
        });

//        saveVenue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getCurrentPlaceItems();
//            }
//        });

        btnChoose = rootView.findViewById(R.id.btnChoose);
        imageView = rootView.findViewById(R.id.eventImage); // Event Image view

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        }); // Select Image listener

        eventDate = rootView.findViewById(R.id.eventDate); // Date picker
        eventTime = rootView.findViewById(R.id.eventTime); // Time picker


        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return rootView;
    }

    private void chooseImage() { // Event Image selector method
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void getCurrentPlaceItems() {
        if (isLocationAccessPermitted()) {
            showPlacePicker();
        } else {
            requestLocationAccessPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void showPlacePicker() {
        Intent i = new Intent(getContext(), LocationPickerActivity.class);
        startActivityForResult(i, ADDRESS_PICKER_REQUEST);

    }

    private boolean isLocationAccessPermitted() {
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_REQ_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }




    // Event save in Firebase and checking Method
    private void saveEvent() {


        if (eventNameInput.getText().toString().isEmpty() ||
                eventDescriptionInput.getText().toString().isEmpty() ||
                spinner.getSelectedItemPosition() < 0 ||
                saveVenue.getText().toString().isEmpty()) {
            ToastHelper.showRedToast(getContext(), "Please fill all the required field.");
            return;
        }

        if (MainActivity.firebaseUser != null) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference ref = database.getReference("users/" + MainActivity.firebaseUser.getUid() + "/events").push().getRef();

            Calendar calendar = new GregorianCalendar(eventDate.getYear(),
                    eventDate.getMonth(),
                    eventDate.getDayOfMonth(),
                    eventTime.getCurrentHour(),
                    eventTime.getCurrentMinute());

            ref.setValue(new Event(eventNameInput.getText().toString(), "No image", eventDescriptionInput.getText().toString(), spinner.getSelectedItemPosition(), calendar.getTime(), saveVenue.getText().toString()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            if (filePath != null) {
                                uploadImage(ref);
                            } else {
                                Toast.makeText(getContext(), "Event is added.",
                                        Toast.LENGTH_SHORT).show();
                                gotoEventListFragment();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            Log.d("Ename", eventNameInput.getText().toString());
        }

    }

    private void gotoEventListFragment() {
//        Fragment fragment = new ViewPagerFragment();
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.container, fragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

        startActivity(new Intent(getContext(), MainActivity.class));

    }

    // Image upload in Firebase method

    private void uploadImage(final DatabaseReference ref) {

        if (filePath != null) {
            storage = FirebaseStorage.getInstance("gs:Your Firebase Storage Link");
            storageReference = storage.getReference();

            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference sRef = storageReference.child("images/" + ref.getKey());
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    ref.child("image").setValue(uri.toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Event is added",
                                                            Toast.LENGTH_SHORT).show();
                                                    gotoEventListFragment();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Error: " + e.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });

                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }
}
