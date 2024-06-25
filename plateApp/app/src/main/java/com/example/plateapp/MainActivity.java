package com.example.plateapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    TextView counter;
    Button reset;

    DatagramSocket UDPSocket;
    Socket TCPSocket;

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
                send = "1";
            }
        });
        TCPThread();
        UDPThread();
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