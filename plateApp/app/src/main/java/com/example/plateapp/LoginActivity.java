package com.example.plateapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private TextView userId, password;
    private ProgressBar progressBar;

    private ServerClient client;

    Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.LoginButton);
        userId = findViewById(R.id.userId);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        client = ServerClient.getClient();

        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onBackPressed() {
        client.close();
        super.onBackPressed();
    }

    public void onLoginClick(View v){
        startConnection();
    }

    public void onContinueAsGuestClick(View view){
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }

    public void startConnection(){
        client.connect();

        new Thread(()->{
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });

            long start = System.currentTimeMillis();
            while ((System.currentTimeMillis() - start) <= 4000){
                if(client.isConnected()){
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });
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
                        jo.put("sender", "app");
                        jo.put("plate_id", plateId);
                        jo.put("user_id", userId.getText());
                        jo.put("user_password", password.getText());
                        Log.d("request", jo.toString());
                        client.writeMessage(jo);
                        JSONObject jsonResponse = client.readMessage();
                        Log.d("response", jsonResponse.toString());
                        if(jsonResponse.get("user_id").toString().contentEquals(userId.getText()) && jsonResponse.get("user_password").toString().contentEquals(password.getText())){
                            client.close();
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
                    return;
                }
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("התחברות כשלה");
                    builder.setMessage("לנסות שנית?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        startConnection();
                    });
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        client.close();
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }).start();
    }
}
