package com.example.myreminder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlarmeDao {
    @Insert
    void insert(Alarme alarme);

    @Query("SELECT * FROM ALARME ORDER BY id DESC")
    List<Alarme>getAllAlarme();

    @Delete
    void deleteAlarme(Alarme alarme);

    @Query("SELECT * from Alarme where id = :param")
    Alarme getAlarme(int param);
}
