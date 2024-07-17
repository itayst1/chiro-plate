package com.example.plateapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button scan;
    private Button toggle;
    private TableLayout items;

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
    }

    @SuppressLint("MissingPermission")
    public void onToggleClick(View view) {
        if(BluetoothController.getInstance().getBluetoothGatt() != null){
            BluetoothController.getInstance().writeCharacteristic("toggle\n\r");
        }
    }

    public void onScanClick(View v) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                | ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 100);
            return;
        }

        BluetoothController.getInstance().disconnect();
        BluetoothController.getInstance().startBluetoothScan();
        scan.setText(R.string.scanning);
        items.removeAllViews();
        scan.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scan.setText(R.string.start_scan);
                Button temp;
                for (BluetoothDevice device : BluetoothController.getInstance().getItemList()) {
                    @SuppressLint("MissingPermission") String name = device.getName();
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
                                if(!BluetoothController.getInstance().getConnectedDevice().equals(((Button) view).getText().toString())) {
                                    ((Button) view).setTextColor(0xFF00FF00);
                                    BluetoothController.getInstance().connectSelected(((Button) view).getText().toString(), MainActivity.this);
                                    BluetoothController.getInstance().setConnectedDevice(((Button) view).getText().toString());
                                }
                                else if(BluetoothController.getInstance().getConnectedDevice().equals(((Button) view).getText().toString())){
                                    BluetoothController.getInstance().disconnect();
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
}