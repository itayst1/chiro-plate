package com.example.plateapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button scan;
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

        bluetoothController = BluetoothController.getInstance();
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
        if(!bluetoothController.startBluetoothScan()) {
            Toast.makeText(MainActivity.this, "בבקשה הדלק בלוטות' ואשר הרשאות.", Toast.LENGTH_SHORT).show();
            return;
        }
        scan.setText(R.string.scanning);
        items.removeAllViews();
        scan.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                scan.setText(R.string.start_scan);
                Button deviceButton;
                for (BluetoothDevice device : bluetoothController.getDevicesList()) {
                    items.addView(createDeviceButton(device.getName()));
                }
                scan.setEnabled(true);
            }
        }, 3000);
    }

    public void onDeviceClick(View view){
        Button button = (Button) view;
        for (int i = 0; i < items.getChildCount(); i++) {
            ((Button) items.getChildAt(i)).setTextColor(0xFFFFFFFF);
            items.getChildAt(i).setEnabled(false);
        }
        if (!bluetoothController.getConnectedDevice().equals(button.getText().toString())) {
            button.setTextColor(0xE000bc65);
            bluetoothController.connectSelected(button.getText().toString(), MainActivity.this);
            bluetoothController.setConnectedDevice(button.getText().toString());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    while((System.currentTimeMillis() - startTime) <= 2000){
                        bluetoothController.writeData("is plate");
                        if(Objects.equals(bluetoothController.readData(), "yes")){
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            return;
                        }
                    }
                    button.setTextColor(0xE0FFFFFF);
                    bluetoothController.disconnect();
                    Toast.makeText(MainActivity.this, "ההתחברות נכשלה", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < items.getChildCount(); i++) {
                        items.getChildAt(i).setEnabled(true);
                    }
                }
            }, 1000);

        } else if (bluetoothController.getConnectedDevice().equals(button.getText().toString())) {
            bluetoothController.disconnect();
        }
    }

    public Button createDeviceButton(String name){
        Button deviceButton = new Button(MainActivity.this);
        deviceButton.setAllCaps(false);
        deviceButton.setText(name);
        deviceButton.setTextSize(35);
        deviceButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        deviceButton.setBackgroundColor(0xE05b5f60);
        deviceButton.setTextColor(0xE0FFFFFF);
        deviceButton.setOnClickListener(this::onDeviceClick);
        return deviceButton;
    }
}