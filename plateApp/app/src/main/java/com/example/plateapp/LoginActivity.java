package com.example.plateapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private TextView userId, password;

    static Socket TCPSocket;
    static OutputStream oos = null;
    static InputStream ois = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.LoginButton);
        userId = findViewById(R.id.userId);
        password = findViewById(R.id.password);

        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try{
            TCPSocket.close();
            oos.close();
            ois.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    public static void connect() throws IOException {
        new Thread(()->{
                try {
                    byte[] buf = new byte[1024];
                    InetAddress address = InetAddress.getByName("192.168.28.170");
                    oos = null;
                    ois = null;
                    // establish socket connection to server
                    TCPSocket = new Socket(address, 12345);
                    //write to socket using ObjectOutputStream
                    oos = TCPSocket.getOutputStream();
                    //read the server response message
                    ois = TCPSocket.getInputStream();
                    //close resources
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        }).start();
    }

    public void onLoginClick(View v){
        try {
            String plateId;
            if(BluetoothController.getInstance().getConnectedDevice().isEmpty()) {
                plateId = "-1";
            }
            else{
                plateId = BluetoothController.getInstance().getConnectedDevice().substring(5);
            }
            JSONObject jo = new JSONObject();
            jo.put("message_type", "0");
            jo.put("plate_id", plateId);
            jo.put("user_id", userId.getText());
            jo.put("user_password", password.getText());
            Log.d("request", jo.toString());
            writeMessage(jo);
            JSONObject jsonResponse = readMessage();
            Log.d("response", jsonResponse.toString());
            if(jsonResponse.get("user_id").toString().contentEquals(userId.getText()) && jsonResponse.get("user_password").toString().contentEquals(password.getText())){
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
            else{
                Toast.makeText(this, "פרטי משתמש אינם נכונים", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "אראה שגיאה, נסה שנית", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeMessage(JSONObject json){
        new Thread(()->{
            Looper.prepare();
            try {
                oos.write(json.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "אראה שגיאה, נסה שנית", Toast.LENGTH_SHORT).show();
            }
            Objects.requireNonNull(Looper.myLooper()).quit();
        }).start();
    }

    private JSONObject readMessage(){
        AtomicReference<JSONObject> json = new AtomicReference<>();
        new Thread(()->{
            Looper.prepare();
            try {
                byte[] buff = new byte[1024];
                int numOfBytes = ois.read(buff);
                byte[] responseBytes = new byte[numOfBytes];
                for(int i = 0; i < numOfBytes; i++){
                    responseBytes[i] = buff[i];
                }
                json.set(new JSONObject(new String(responseBytes)));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "אראה שגיאה, נסה שנית", Toast.LENGTH_SHORT).show();
            }
            Objects.requireNonNull(Looper.myLooper()).quit();
        }).start();
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis() - startTime) <= 600){
        }
        return json.get();
    }

    public void onContinueAsGuestClick(View view){
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }
}
