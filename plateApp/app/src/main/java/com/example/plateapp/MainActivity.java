package com.example.plateapp;

import static android.nfc.NfcAdapter.EXTRA_DATA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button scan;
    private Button toggle;
    private TableLayout items;
    private BluetoothUtils bluetoothUtils = new BluetoothUtils();

    BroadcastReceiver disconnectBroadcastReceiver;

    private BluetoothGatt bluetoothGatt = null;

    private String connectedDevice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        items = (TableLayout) findViewById(R.id.items);
        items.setPadding(0, 30, 0, 0);
        scan = (Button) findViewById(R.id.scan);
        toggle = (Button) findViewById(R.id.toggle);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                        | ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 100);
                    return;
                }

                disconnect();
                bluetoothUtils.startBluetoothScan();
                scan.setText("scanning...");
                items.removeAllViews();
                scan.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scan.setText("start scan");
                        Button temp;
                        for (BluetoothDevice device : bluetoothUtils.getItemList()) {
                            @SuppressLint("MissingPermission") String name = device.getName().toString();
                            if (name != null) {
                                temp = new Button(MainActivity.this);
                                temp.setAllCaps(false);
                                temp.setText(name);
                                temp.setTextSize(35);
                                temp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                temp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        for(int i = 0; i < items.getChildCount(); i++){
                                            ((Button)items.getChildAt(i)).setTextColor(0xFFFFFFFF);
                                        }
                                        if(!connectedDevice.equals(((Button) view).getText().toString())) {
                                            ((Button) view).setTextColor(0xFF00FF00);
                                            connectSelected(((Button) view).getText().toString());
                                            connectedDevice = ((Button) view).getText().toString();
                                        }
                                        else if(connectedDevice.equals(((Button) view).getText().toString())){
                                            disconnect();
                                        }
                                    }
                                });
                                items.addView(temp);
                            }
                        }
                        scan.setEnabled(true);
                    }
                }, 5400);
            }
        });
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bluetoothGatt != null){
                    Log.d("debug", "toggle");
                }
            }
        });
        disconnectBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(bluetoothGatt != null)
                    disconnect(); // disconnect now, else would be queued until UI re-attached
            }
        };
    }


    @SuppressLint("MissingPermission")
    private void connectSelected(String name){
        for(BluetoothDevice device : bluetoothUtils.getItemList()){
            if(device.getName().equals(name)){
                disconnect();
                ContextCompat.registerReceiver(this, disconnectBroadcastReceiver, new IntentFilter("Disconnect"), ContextCompat.RECEIVER_NOT_EXPORTED);
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void disconnect(){
        if(bluetoothGatt != null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
            this.unregisterReceiver(disconnectBroadcastReceiver);
            connectedDevice = "";
        }
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
            }
        }
    };

    @SuppressLint("MissingPermission")
    public void writeCharacteristic() {

    }
}