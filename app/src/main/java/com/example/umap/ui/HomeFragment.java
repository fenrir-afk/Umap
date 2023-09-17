package com.example.umap.ui;

import static com.yandex.runtime.Runtime.getApplicationContext;



import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.umap.MainActivity;
import com.example.umap.R;
import com.example.umap.data.models.MainData;
import com.example.umap.data.room.RoomDB;
import com.example.umap.databinding.FragmentHomeBinding;
import com.example.umap.ui.state_holder.HomeViewModel;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final double DESIRED_ACCURACY = 0;
    private static final long MINIMAL_TIME = 0;
    private static final double MINIMAL_DISTANCE = 0;
    private static final boolean USE_IN_BACKGROUND = false;
    public static final int COMFORTABLE_ZOOM_LEVEL = 18;
    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private Point myLocation;
    private PlacemarkMapObject Now_Geoposition;
    RoomDB database;
    Point PointForDistance;
    private Boolean initialized = false;
    public int i = 0;
    public List<String> list;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        list  = new ArrayList<String>();
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        homeViewModel.checkPermission(getContext());
        locationManager = MapKitFactory.getInstance().createLocationManager();
        //Получение текущих координат в реальном времени
        myLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (myLocation == null) {
                    moveCamera(location.getPosition(), COMFORTABLE_ZOOM_LEVEL);
                    myLocation = location.getPosition();
                    binding.Latitude.setText(Double.toString(myLocation.getLatitude()));
                    binding.Longitude.setText(Double.toString(myLocation.getLongitude()));
                    Now_Geoposition = binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(myLocation, ImageProvider.fromResource(getApplicationContext(), R.drawable.arrow));
                }

            }
            @Override
            public void onLocationStatusUpdated(LocationStatus locationStatus) {
                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                    System.out.println("LocationStatus is NotAvailable");
                }
            }
        };
        //Кнопка отвечающая за добавление маркера на карту, проложения к ней пути
        // ,добавление в списки маркера(после добавления)
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAlertDialog();
            }
        });
        //Реализация списка с активными(текущими) метками
        list.add("Active marks:");
        ArrayAdapter<String> autoBrandsAdapter = new ArrayAdapter<String>(getContext(), R.layout.my_selected_item,list);
        autoBrandsAdapter.setDropDownViewResource(R.layout.my_dropdown_item);
        binding.ActiveMarkers.setAdapter(autoBrandsAdapter);
        //При нажатии на элемент списка должно выводить расстоние до маркера
        binding.ActiveMarkers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] arr = list.get(position).split(" ");
                if (arr.length > 3){
                    PointForDistance =  new Point(Double.parseDouble(arr[2]),Double.parseDouble(arr[5]));
                    homeViewModel.getDistance(Double.parseDouble(arr[2]),myLocation.getLongitude(),Double.parseDouble(arr[5]),myLocation.getLatitude(),0.0,0.0).observe(getViewLifecycleOwner(),new Observer<Double>() {
                        @Override
                        public void onChanged(Double aDouble) {
                            Toast.makeText(getApplicationContext(),   String.format("%.1f",aDouble) + " км", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        binding.mapview.onStart();
        subscribeToLocationUpdate();
    }
    public void AddAlertDialog() {
        final Dialog addDialog = new Dialog(getContext());
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addDialog.setContentView(R.layout.item_layout);
        addDialog.setTitle("Add marker");
        EditText edit1 = (EditText) addDialog.findViewById(R.id.edit1);
        EditText edit2 = (EditText) addDialog.findViewById(R.id.edit2);
        Button add = addDialog.findViewById(R.id.button1);
        Button current = (Button) addDialog.findViewById(R.id.current);
        CheckBox box = (CheckBox)addDialog.findViewById(R.id.box);
        //Добавление маркера
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //добавление маркера на карту
                    String  coord1 = edit1.getText().toString();
                    String coord2 = edit2.getText().toString();
                    Double latitude = Double.parseDouble(coord1);
                    Double longitude = Double.parseDouble(coord2);
                    list.add("Coordinates: latitude: " + coord1 + "  longitude: " + coord2);
                    binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(
                                new Point(latitude,longitude),
                                ImageProvider.fromResource(getApplicationContext(),
                                        R.drawable.arrow));
                        //Добаввляем всё это в базу данных маркеров(в оба списка)
                        HomeViewModel homeViewModel = new HomeViewModel();
                        homeViewModel.addToMarkerDatabase(latitude,longitude,getContext());
                } catch (NumberFormatException e) {
                    edit1.setHint("Нужно ввести координату");
                    edit1.setHintTextColor(Color.RED);
                    edit2.setHint("Нужно ввести координату");
                    edit2.setHintTextColor(Color.RED);
                }
            }
        });
        //Добавление текущего положения
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.mapview.getMapWindow().getMap().getMapObjects().addPlacemark(myLocation, ImageProvider.fromResource(getApplicationContext(), R.drawable.arrow));
                binding.Longitude.setText(String.valueOf(myLocation.getLongitude()));
                binding.Latitude.setText(String.valueOf(myLocation.getLatitude()));
            }
        });
        //Проложение пути
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String coord1 = edit1.getText().toString();
                    String  coord2 = edit2.getText().toString();
                    Double latitude = Double.parseDouble(coord1);
                    Double longitude = Double.parseDouble(coord2);

                    //Добавление polyline на карту

                    HomeViewModel homeViewModel = new HomeViewModel();
                    homeViewModel.createPolyline(latitude,longitude,myLocation);
                    Polyline pl = homeViewModel.createPolyline(latitude,longitude,myLocation);
                    binding.mapview.getMap().getMapObjects().addPolyline(pl);
                } catch (NumberFormatException e) {
                    edit1.setHint("Нужно ввести координату");
                    edit1.setHintTextColor(Color.RED);
                    edit2.setHint("Нужно ввести координату");
                    edit2.setHintTextColor(Color.RED);
                }
            }
        });
        addDialog.show();
    }


    @Override
    public void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        locationManager.unsubscribe(myLocationListener);
        binding.mapview.onStop();
    }
    private void subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, myLocationListener);
        }
    }

    private void moveCamera(Point point, float zoom) {
        binding.mapview.getMap().move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);
    }


}