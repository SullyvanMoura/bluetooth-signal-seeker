package com.ufrrj.bluetooth_signal_seeker.Databases;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ufrrj.bluetooth_signal_seeker.Data.Dispositivo;

import java.util.List;

@Dao
public interface DispositivoDao {

    @Query("SELECT * FROM dispositivos ORDER BY nome ASC")
    List<Dispositivo> getAll();

    @Query("SELECT * FROM dispositivos WHERE nome IN (:dispositivoIds)")
    List<Dispositivo> loadAllByIds(String[] dispositivoIds);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Dispositivo dispositivo);

}
