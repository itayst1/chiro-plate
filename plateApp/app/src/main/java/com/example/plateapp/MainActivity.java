package com.example.plateapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    TextView counter;
    DatagramSocket client_socket;

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
        clientThread();
    }

    byte[] buf;

    public void clientThread(){
        new Thread(()->{
            try {
                client_socket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("192.168.4.1");
                buf = new byte[1024];
                DatagramPacket send_packet = new DatagramPacket(buf, 1024, IPAddress, 12000);
                client_socket.send(send_packet);
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                while(true){
                    client_socket.receive(receivePacket);
                    String received = new String(
                            receivePacket.getData(), 0, receivePacket.getLength());
                    counter.setText(received);
                }
            }
            catch (Exception e){
                e.printStackTrace();
                counter.setText("-2");
            }
        }).start();
    }
}