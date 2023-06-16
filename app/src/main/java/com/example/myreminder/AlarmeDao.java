package com.example.myreminder;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlarmeDao {
    @Insert
    void insert(Alarme alarme);

    @Query("SELECT * FROM ALARME")
    List<Alarme>getAllAlarme();
}
