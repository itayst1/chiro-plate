package com.example.plateapp;

import android.os.Looper;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ServerClient {

    private static final ServerClient m_instance = new ServerClient();

    private static Socket TCPSocket;
    private static OutputStream oos = null;
    private static InputStream ois = null;

    private ServerClient(){
    }

    public static ServerClient getClient(){
        return m_instance;
    }

    public void connect() {
        m_instance.close();
        new Thread(()->{
            try {
                byte[] buf = new byte[1024];
                InetAddress address = InetAddress.getByName("192.168.28.170");
                // establish socket connection to server
                TCPSocket = new Socket(address, 12345);
                //write to socket using ObjectOutputStream
                oos = TCPSocket.getOutputStream();
                //read the server response message
                ois = TCPSocket.getInputStream();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void close(){
        try{
            TCPSocket.close();
            oos.close();
            ois.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        if(TCPSocket == null)
            return false;
        try {
            return TCPSocket.isConnected();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void writeMessage(JSONObject json){
        new Thread(()->{
            Looper.prepare();
            try {
                oos.write(json.toString().getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Objects.requireNonNull(Looper.myLooper()).quit();
        }).start();
    }

    public JSONObject readMessage(){
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
            }
            Objects.requireNonNull(Looper.myLooper()).quit();
        }).start();
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis() - startTime) <= 600){
        }
        return json.get();
    }
}
