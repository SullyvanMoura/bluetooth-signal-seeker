package Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dispositivos")
public class Dispositivo {

    @PrimaryKey
    @NonNull
    public String nome;
}
