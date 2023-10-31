package Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.List;

@Database(entities = {Dispositivo.class}, version = 1, exportSchema = false)
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
