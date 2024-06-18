package com.example.plateapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP_Client {

    public UDP_Client(TextView count){
        counter = count;
    }
    private InetAddress IPAddress = null;
    private String message = "Hello Android!" ;
    private AsyncTask<Void, Void, Void> async_cient;
    public static String Message;

    public static TextView counter;


    @SuppressLint("NewApi")
    public void send() {
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params)
            {
                DatagramSocket socket = null;

                try {
                    InetAddress addr = InetAddress.getByName("192.168.4.1");
                    socket = new DatagramSocket(12000);
                    DatagramPacket packet;
                    packet = new DatagramPacket(Message.getBytes(), Message.getBytes().length, addr, 12000);
                    socket.send(packet);
                    while(true){
                        socket.receive(packet);
                        String received = new String(
                                packet.getData(), 0, packet.getLength());
                        System.out.println(received);
                        counter.setText(received);
                    }
                }
                catch (Exception e)
                {
                    counter.setText("-2");
                    e.printStackTrace();
                }
                finally
                {
                    if (socket != null)
                    {
                        socket.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }
        };
        async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
