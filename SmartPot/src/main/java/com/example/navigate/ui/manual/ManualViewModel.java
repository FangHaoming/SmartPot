package com.example.navigate.ui.manual;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ManualViewModel extends ViewModel{


    private MutableLiveData<String> mText;

    public ManualViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is manual fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}