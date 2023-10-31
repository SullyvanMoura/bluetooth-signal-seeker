package com.ufrrj.bluetooth_signal_seeker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

import Data.Dispositivo;

public class DispositivoAdapter extends ArrayAdapter<Dispositivo> {

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

        textViewNome.setText(dispositivo.getNome());
        textViewEndereco.setText("MAC: " + dispositivo.getEndereço());

        return convertView;
    }
}
