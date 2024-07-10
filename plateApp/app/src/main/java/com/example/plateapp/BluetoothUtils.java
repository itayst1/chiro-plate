package com.example.plateapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.core.app.ActivityCompat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BluetoothUtils {

    BluetoothAdapter bluetoothAdapter;
    OutputStream os;
    InputStream is;

    private ArrayList<BluetoothDevice> listItems = new ArrayList<>();

    public BluetoothUtils() {
    }

    public void startBluetoothScan() {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                return;
            }
            scanLeDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean scanning = false;
    private Handler handler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;

    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
        new Thread(() -> {
            if (!scanning) {
                // Stops scanning after a predefined scan period.
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanning = false;
                        bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
                    }
                }, SCAN_PERIOD);

                scanning = true;
                listItems.clear();
                bluetoothAdapter.getBluetoothLeScanner().startScan(leScanCallback);
            } else {
                scanning = false;
                bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
            }
        }).start();
    }

    @SuppressLint("MissingPermission")
    private final ScanCallback leScanCallback = new ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String name = result.getDevice().getName();
            if (name != null && !listItems.contains(result.getDevice())) {
                listItems.add(result.getDevice());
            }
        }
    };

    public ArrayList<BluetoothDevice> getItemList() {
        return listItems;
    }
}
