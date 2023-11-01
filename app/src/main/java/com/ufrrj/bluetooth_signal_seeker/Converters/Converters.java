package com.ufrrj.bluetooth_signal_seeker.Converters;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import java.time.LocalDateTime;

public class Converters {

    @TypeConverter
    public String fromLocalDateTime(@NonNull LocalDateTime date) {

        return date.toString();
    }

    @TypeConverter
    public LocalDateTime toLocalDateTime(String date) {

            return LocalDateTime.parse(date);
    }
}
