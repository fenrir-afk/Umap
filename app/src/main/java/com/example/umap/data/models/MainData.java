package com.example.umap.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_name")
public class MainData {
    @PrimaryKey(autoGenerate = true)
    public int ID;
    @ColumnInfo(name ="latitude")
    public Double latitude;
    @ColumnInfo(name ="longitude")
    public Double longitude;

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getID() {
        return ID;
    }

    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
}
