package com.example.umap.ui.state_holder;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umap.data.models.MainData;
import com.example.umap.data.room.RoomDB;

import java.util.ArrayList;
import java.util.List;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;


    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public void addData(Double latitude, Double longitude, Context context){
        RoomDB database = RoomDB.getInstance(context);
        MainData data = new MainData();
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        database.userDao().insert(data);

    }
}