package com.ufrrj.bluetooth_signal_seeker.Data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "dispositivos")
public class Dispositivo implements Comparable<Dispositivo> {

    @PrimaryKey
    @NonNull
    public String endereço;

    @NonNull
    public String nome;

    @NonNull
    private LocalDateTime data;

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

    @NonNull
    public LocalDateTime getData() {
        return data;
    }

    public void setData(@NonNull LocalDateTime data) {
        this.data = data;
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

    @Override
    public int compareTo(Dispositivo outroDispositivo) {

        LocalDateTime dataDoOutro = outroDispositivo.getData();
        if (dataDoOutro.isAfter(this.getData())) {
            return -1;
        }
        else {
            return 1;
        }
    }
}


