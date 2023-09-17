package com.example.umap.data.room;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.umap.data.models.MainData;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void insert(MainData mainData);
    @Delete
    void delete(MainData mainData);
    @Delete
    void reset(List<MainData> people);
    @Query("SELECT * FROM table_name")
    List<MainData> getAll();
}
