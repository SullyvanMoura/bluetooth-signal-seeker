package com.ufrrj.bluetooth_signal_seeker;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ufrrj.bluetooth_signal_seeker.Adapters.DispositivoAdapter;
import com.ufrrj.bluetooth_signal_seeker.Data.Dispositivo;
import com.ufrrj.bluetooth_signal_seeker.Databases.DispositivoDao;
import com.ufrrj.bluetooth_signal_seeker.Databases.DispositivoDatabase;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_BLUETOOTH_CONNECT = 1;
    public static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 2;
    public static final int REQUEST_CODE_BLUETOOTH_ADMIN = 3;
    public static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 4;
    public static final int REQUEST_CODE_BLUETOOTH_SCAN = 5;
    public static final int REQUEST_CHECK_SETTING = 1001;
    public static final String TAG = "BLUETOOTH DEVICES";

    private ListView lv;
    private Button scanBtn;
    private Button stopBtn;
    private TextView textViewDevicesNumber;
    private BroadcastReceiver mbroadcastReceiver;
    private BluetoothAdapter bluetoothAdapter;

    private LocationRequest locationRequest;
    private ArrayList<Dispositivo> dispositivosEncontrados = new ArrayList<>();

    private DispositivoDao dispositivoDao;
    private Thread buscarDispositivosThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.listView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        DispositivoDatabase instance = DispositivoDatabase.getInstance(getApplicationContext());
        dispositivoDao = instance.getDispositivoDao();

        dispositivosEncontrados.addAll(dispositivoDao.getAll());
        Collections.sort(dispositivosEncontrados, Collections.reverseOrder());

        DispositivoAdapter itemAdapter = new DispositivoAdapter(this, R.layout.item_dispositivo, dispositivosEncontrados);
        lv.setAdapter(itemAdapter);

        solicitarAtivacaoBluetooth();
        solicitarAtivacaoLocalizacao();

        textViewDevicesNumber = findViewById(R.id.textViewDevicesNumber);
        textViewDevicesNumber.setText("N° de Dispositivos encontrados: " + dispositivosEncontrados.size());

        scanBtn = findViewById(R.id.scanBtn);
        stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setEnabled(false);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarBusca();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finalizarBusca();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        solicitarPermissões(MainActivity.this, this);

        mbroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE_BLUETOOTH_CONNECT);
                    }

                    if (device.getName() != null) {

                        Log.i(TAG, device.getName());
                        Dispositivo dispositivo = new Dispositivo();
                        dispositivo.setNome(device.getName());
                        dispositivo.setEndereço(device.getAddress());
                        dispositivo.setData(LocalDateTime.now());

                        if (!dispositivosEncontrados.contains(dispositivo)) {
                            Log.i(TAG, "Novo dispositivo");
                            dispositivoDao.insert(dispositivo);
                            dispositivosEncontrados.add(0, dispositivo);

                            DispositivoAdapter itemAdapter = new DispositivoAdapter(getApplicationContext(), R.layout.item_dispositivo, dispositivosEncontrados);
                            lv.setAdapter(itemAdapter);
                            textViewDevicesNumber.setText("N° de Dispositivos encontrados: " + dispositivosEncontrados.size());
                        }

                    }
                    else {
                        Log.e(TAG, "Dispositivo encontrado não possui nome");
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mbroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mbroadcastReceiver);

        finalizarBusca();

    }

    @Override
    protected void onPause() {
        super.onPause();

        finalizarBusca();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTING) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, "Localização ligada!", Toast.LENGTH_SHORT).show();
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "A localização precisa estar ligado para o funcionamento do aplicativo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void solicitarPermissões(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_CODE_BLUETOOTH_ADMIN);
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_BLUETOOTH_SCAN);
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE_BLUETOOTH_CONNECT);
        }
    }

    private void solicitarAtivacaoBluetooth() {

        if (bluetoothAdapter == null) {

            Toast.makeText(MainActivity.this, "BLUETOOTH NÃO SUPORTADO", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE_BLUETOOTH_CONNECT);
                }
                startActivity(enableBtIntent);
            }
        }
    }

    private void solicitarAtivacaoLocalizacao() {
        locationRequest = new LocationRequest.Builder(5000).build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    //Toast.makeText(MainActivity.this, "GPS está ligado", Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTING);
                            } catch (IntentSender.SendIntentException ex) {}
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    private static void buscarDispositivos(Context context, Activity activity, BluetoothAdapter adapter) {

        Log.d(TAG, "Iniciando Thread de Busca de Dispositivo");

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_BLUETOOTH_SCAN);
        }
        while (true) {
            adapter.startDiscovery();
            try {
                Thread.sleep(12000);
            } catch (InterruptedException e) {
                Log.d(TAG, "Thread interromida");
                break;
            }
        }
    }

    private void iniciarBusca() {
        solicitarAtivacaoBluetooth();
        solicitarAtivacaoLocalizacao();

        buscarDispositivosThread = new Thread(() -> {
            buscarDispositivos(MainActivity.this, MainActivity.this, bluetoothAdapter);
        });

        Log.i("BLUETOOTH DEVICES", "Iniciando busca...");
        if (!buscarDispositivosThread.isAlive()) {
            buscarDispositivosThread.start();
        }

        scanBtn.setEnabled(false);
        scanBtn.setText("Scanning...");
        stopBtn.setEnabled(true);
    }

    private void finalizarBusca() {
        Log.i("BLUETOOTH DEVICES", "Finalizando Busca...");
        if (buscarDispositivosThread != null) {
            if (buscarDispositivosThread.isAlive()) {
                buscarDispositivosThread.interrupt();
            }

            scanBtn.setEnabled(true);
            scanBtn.setText("Scan");
            stopBtn.setEnabled(false);
        }
    }
}