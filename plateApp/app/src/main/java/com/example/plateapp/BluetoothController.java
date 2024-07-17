package com.example.plateapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.UUID;

public class BluetoothController {

    private static BluetoothController m_instance = null;

    private BluetoothAdapter m_bluetoothAdapter;

    private ArrayList<BluetoothDevice> m_devicesList;

    public static BluetoothController getInstance(){
        if(m_instance == null)
            m_instance = new BluetoothController();
        return m_instance;
    }

    private BluetoothController() {
        m_devicesList = new ArrayList<>();
    }

    public void startBluetoothScan() {
        try {
            m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!m_bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                return;
            }
            scanLeDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean scanning = false;

    // Stops scanning after 5 seconds.
    private static final long SCAN_PERIOD = 5000;

    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
        new Thread(() -> {
            if (!scanning) {
                // Stops scanning after a predefined scan period.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanning = false;
                        m_bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
                    }
                }, SCAN_PERIOD);

                scanning = true;
                m_devicesList.clear();
                m_bluetoothAdapter.getBluetoothLeScanner().startScan(leScanCallback);
            } else {
                scanning = false;
                m_bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
            }
        }).start();
    }

    @SuppressLint("MissingPermission")
    private final ScanCallback leScanCallback = new ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String name = result.getDevice().getName();
            if (name != null && !m_devicesList.contains(result.getDevice())) {
                m_devicesList.add(result.getDevice());
            }
        }
    };

    public ArrayList<BluetoothDevice> getItemList() {
        return m_devicesList;
    }


    //gatt

    private final BroadcastReceiver disconnectBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(m_bluetoothGatt != null)
                disconnect(); // disconnect now, else would be queued until UI re-attached
        }
    };;

    private BluetoothGatt m_bluetoothGatt = null;

    private String m_connectedDevice = "";

    private BluetoothGattCharacteristic m_writeCharacteristic, m_readCharacteristic;

    public BluetoothGatt getBluetoothGatt() {return m_bluetoothGatt;}

    public String getConnectedDevice() {return m_connectedDevice;}
    public void setConnectedDevice(String device) {m_connectedDevice = device;}

    @SuppressLint("MissingPermission")
    public void connectSelected(String name, Context context){
        for(BluetoothDevice device : BluetoothController.getInstance().getItemList()){
            if(device.getName().equals(name)){
                disconnect();
                ContextCompat.registerReceiver(context, disconnectBroadcastReceiver, new IntentFilter("Disconnect"), ContextCompat.RECEIVER_NOT_EXPORTED);
                m_bluetoothGatt = device.connectGatt(context, false, bluetoothGattCallback);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void disconnect(){
        if(m_bluetoothGatt != null){
            m_bluetoothGatt.disconnect();
            m_bluetoothGatt.close();
            m_bluetoothGatt = null;
            m_connectedDevice = "";
        }
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                m_bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
            }
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
            Log.d("read", characteristic.getStringValue(0));
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                m_writeCharacteristic = m_bluetoothGatt.getServices().get(2).getCharacteristics().get(1);
                m_writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

                m_readCharacteristic = m_bluetoothGatt.getServices().get(2).getCharacteristics().get(0);
                m_readCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                m_bluetoothGatt.setCharacteristicNotification(m_readCharacteristic, true);
                BluetoothGattDescriptor desc = m_readCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(desc);
                Log.i("characteristics", "done");
            } else {
                Log.w("TAG", "onServicesDiscovered received: " + status);
            }
        }
    };

    @SuppressLint("MissingPermission")
    public void writeCharacteristic(String data) {
        m_writeCharacteristic.setValue(data.getBytes());
        m_bluetoothGatt.writeCharacteristic(m_writeCharacteristic);
    }


}
