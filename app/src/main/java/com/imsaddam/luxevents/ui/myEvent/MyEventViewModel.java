package com.imsaddam.luxevents.ui.myEvent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyEventViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is myEvent fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}