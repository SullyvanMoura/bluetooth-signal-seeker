package Data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dispositivos")
public class Dispositivo {

    @PrimaryKey
    @NonNull
    public String endereço;

    @NonNull
    public String nome;

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    @NonNull
    public String getEndereço() {
        return endereço;
    }

    public void setEndereço(@NonNull String endereço) {
        this.endereço = endereço;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Dispositivo)) {
            return false;
        }
        Dispositivo outroDispositivo = (Dispositivo) obj;
        if (outroDispositivo.getEndereço().equals(this.getEndereço())) {
            return true;
        }
        else {
            return false;
        }
    }
}


