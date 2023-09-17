package com.example.umap.ui.state_holder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umap.MainActivity;
import com.example.umap.data.models.MainData;
import com.example.umap.data.room.RoomDB;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    RoomDB database;

    public LiveData<Double> getDistance(Double lat1, Double lat2, Double lon1,
                                        Double lon2, Double el1, Double el2) {
        final MutableLiveData<Double> distanceFinal = new MutableLiveData<Double>();
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        distanceFinal.setValue( Math.sqrt(distance)/1000);
        return distanceFinal;
    }
    public void addToMarkerDatabase(Double latitude, Double longitude, Context context){
        database = RoomDB.getInstance(context);
        MainData data = new MainData();
        data.setLongitude(longitude);
        data.setLatitude(latitude);
        database.userDao().insert(data);

    }
    public Polyline createPolyline(Double latitude, Double longitude, Point myLocation){
        List<Point> points = new ArrayList<>();
        points.add(myLocation);
        points.add(new Point(latitude,longitude));
        Polyline pl = new Polyline(points);
        return pl;

    }
    public void  checkPermission(Context context){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(context, MainActivity.class);
                context.startActivity(myIntent);
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("To use the app, you must provide access to the location\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }



}