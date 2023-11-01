package com.ufrrj.bluetooth_signal_seeker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import com.ufrrj.bluetooth_signal_seeker.Data.Dispositivo;
import com.ufrrj.bluetooth_signal_seeker.R;

public class DispositivoAdapter extends ArrayAdapter<Dispositivo> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

    public DispositivoAdapter(@NonNull Context context, int resource, @NonNull List<Dispositivo> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_dispositivo, parent, false);
        }

        Dispositivo dispositivo = getItem(position);

        TextView textViewNome = convertView.findViewById(R.id.textViewNome);
        TextView textViewEndereco = convertView.findViewById(R.id.textViewEndereco);
        TextView textViewData = convertView.findViewById(R.id.textViewData);

        textViewNome.setText(dispositivo.getNome());
        textViewEndereco.setText("MAC: " + dispositivo.getEndereço());
        textViewData.setText("Encontrado em: " + formatter.format(dispositivo.getData()));

        return convertView;
    }
}
