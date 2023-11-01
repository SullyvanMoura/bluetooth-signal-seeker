package com.ufrrj.bluetooth_signal_seeker.Databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ufrrj.bluetooth_signal_seeker.Converters.Converters;
import com.ufrrj.bluetooth_signal_seeker.Data.Dispositivo;

@Database(entities = {Dispositivo.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class DispositivoDatabase extends RoomDatabase {

    public abstract DispositivoDao getDispositivoDao();

    private static final String DB_NAME = "dispositivoDatabase.db";
    private static volatile DispositivoDatabase instance;

    public static synchronized DispositivoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static DispositivoDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                DispositivoDatabase.class,
                DB_NAME).allowMainThreadQueries().build();
    }

}
