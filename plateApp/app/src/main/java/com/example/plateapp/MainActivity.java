package com.example.plateapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    TextView counter;
    Button reset;

    DatagramSocket UDPSocket;
    Socket TCPSocket;

    BluetoothSocket bluetoothSocket;
    OutputStream os;
    InputStream is;

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
        counter = (TextView) findViewById(R.id.counter);
        counter.setText("-1");
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                send = "1";
                try {
//                    os.write("toggle".getBytes());
                } catch (Exception e) {
//                    throw new RuntimeException(e);
                }
            }
        });
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                counter.setText("10");
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.BLUETOOTH_CONNECT}, 101);
                    }
                    try {
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
//        TCPThread();
//        UDPThread();
        bluetoothThread();
    }

    private void bluetoothThread() {
        new Thread(() -> {
            try {
                BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                }
                counter.setText("7");
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.BLUETOOTH_SCAN}, 100);
                }
                bluetoothAdapter.startDiscovery();
                counter.setText("5");
                Thread.sleep(10000);

//                BluetoothDevice device = (BluetoothDevice) bluetoothAdapter.getBondedDevices().toArray()[0];
//                socket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
//                socket.connect();
                counter.setText("3");
                os = bluetoothSocket.getOutputStream();
                is = bluetoothSocket.getInputStream();
            }
            catch(Exception e){
                e.printStackTrace();
                counter.setText("4");
            }
        }).start();
    }

    private int UDPPort = 12000;
    private int TCPPort = 5000;
    private String send = "0";

    private void UDPThread(){
        new Thread(()->{
            try {
                Thread.sleep(100);
                byte[] buf;
                UDPSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("192.168.4.1");
                buf = new byte[1024];
                DatagramPacket send_packet = new DatagramPacket(buf, 1024, IPAddress, UDPPort);
                UDPSocket.send(send_packet);
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                while(true){
                    UDPSocket.receive(receivePacket);
                    String received = new String(
                            receivePacket.getData(), 0, receivePacket.getLength());
                    counter.setText(received);

                    byte[] bytes = send.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, IPAddress, UDPPort);
                    UDPSocket.send(sendPacket);
                    if(send.equals("1")) send = "0";
                }
            }
            catch (Exception e){
                e.printStackTrace();
                counter.setText("-2");
            }
        }).start();
    }

    private void TCPThread(){
        new Thread(()->{
            try {
                byte[] buf = new byte[1024];
                InetAddress address = InetAddress.getByName("192.168.4.1");;
                ObjectOutputStream oos = null;
                InputStream ois = null;
                // establish socket connection to server
                TCPSocket = new Socket(address, TCPPort);
                //write to socket using ObjectOutputStream
                oos = new ObjectOutputStream(TCPSocket.getOutputStream());
                oos.writeObject(null);
                //read the server response message
                ois = TCPSocket.getInputStream();
                int message = ois.read();
            }
            catch (Exception e){
                e.printStackTrace();
                counter.setText("-3");
            }
        }).start();
    }
}