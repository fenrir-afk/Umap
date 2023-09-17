package com.example.umap.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.umap.adapters.MainAdapter;
import com.example.umap.data.models.MainData;
import com.example.umap.data.room.RoomDB;
import com.example.umap.databinding.FragmentGalleryBinding;
import com.example.umap.ui.state_holder.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    List<MainData> dataList = new ArrayList<>();
    RoomDB database;
    MainAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RoomDB database = RoomDB.getInstance(getContext());
        dataList = database.userDao().getAll();
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getContext());///
        binding.recyclerView.setLayoutManager(LinearLayoutManager);
        adapter = new MainAdapter(getActivity(),dataList);////
        binding.recyclerView.setAdapter(adapter);
        binding.btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double latitude = Double.parseDouble(String.valueOf(binding.latitude.getText()));
                    Double longitude = Double.parseDouble(String.valueOf(binding.Longitude.getText()));
                    if (latitude != null){
                        galleryViewModel.addData(latitude,longitude,getContext());
                        binding.latitude.setText("");
                        binding.Longitude.setText("");
                        dataList.clear();
                        dataList.addAll(database.userDao().getAll());
                        adapter.notifyDataSetChanged();
                    }
                }catch (NumberFormatException e) {
                    binding.latitude.setHint("Нужно ввести координату");
                    binding.latitude.setHintTextColor(Color.RED);
                    binding.Longitude.setHint("Нужно ввести координату");
                    binding.Longitude.setHintTextColor(Color.RED);
                }
            }
        });
        binding.btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.userDao().reset(dataList);
                dataList.clear();
                dataList.addAll(database.userDao().getAll());
                adapter.notifyDataSetChanged();
            }
        });

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}