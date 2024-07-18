package com.example.plateapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button scan;
    private Button toggle;
    private TableLayout items;

    private BluetoothController bluetoothController;

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

        bluetoothController = BluetoothController.getInstance();
    }

    @SuppressLint("MissingPermission")
    public void onToggleClick(View view) {
        if(bluetoothController.getBluetoothGatt() != null){
            bluetoothController.writeCharacteristic("toggle\n\r");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void onScanClick(View v) {
        //check for permissions and if not found return.
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                | ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 100);
            return;
        }

        bluetoothController.disconnect();
        if(!bluetoothController.startBluetoothScan())
            return;
        scan.setText(R.string.scanning);
        items.removeAllViews();
        scan.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                scan.setText(R.string.start_scan);
                Button deviceButton;
                for (BluetoothDevice device : bluetoothController.getDevicesList())
                    items.addView(createDeviceButton(device.getName()));
                scan.setEnabled(true);
            }
        }, 5400);
    }

    public void onDeviceClick(View view){
        Button button = (Button) view;
        for (int i = 0; i < items.getChildCount(); i++) {
            ((Button) items.getChildAt(i)).setTextColor(0xFFFFFFFF);
        }
        if (!bluetoothController.getConnectedDevice().equals(button.getText().toString())) {
            button.setTextColor(0xFF00FF00);
            bluetoothController.connectSelected(button.getText().toString(), MainActivity.this);
            bluetoothController.setConnectedDevice(button.getText().toString());
            Toast.makeText(MainActivity.this, "connecting...", Toast.LENGTH_SHORT).show();
        } else if (bluetoothController.getConnectedDevice().equals(button.getText().toString())) {
            bluetoothController.disconnect();
            Toast.makeText(MainActivity.this, "disconnected", Toast.LENGTH_SHORT).show();
        }
    }

    public Button createDeviceButton(String name){
        Button deviceButton = new Button(MainActivity.this);
        deviceButton.setAllCaps(false);
        deviceButton.setText(name);
        deviceButton.setTextSize(35);
        deviceButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        deviceButton.setOnClickListener(this::onDeviceClick);
        return deviceButton;
    }
}