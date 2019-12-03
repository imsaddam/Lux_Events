package com.imsaddam.luxevents.ui.createEvent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateEventViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CreateEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Create Your Event");
    }

    public LiveData<String> getText() {
        return mText;
    }
}