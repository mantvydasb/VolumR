package com.example.mantvydas.volumr;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by mantvydas on 10/13/2015.
 */
public class ServerConnection {
    private final String STOP_SERVER = "STOP_SERVER";
    private Socket socket;
    private String shortIPAddress;
    private String IPAddress = null;
    private Context context;
    private OnConnectionListener onConnectionListener;

    /**
     * Connects to a a server - user's PC which is on the same network as user's Android device
     * @param onConnectionListener - listener for connection related events
     * @param context required retrieving device's IP address
     */
    public ServerConnection(OnConnectionListener onConnectionListener, Context context) {
        this.onConnectionListener = onConnectionListener;
        this.context = context;
        connectToPc();
    }

    public void connectToPc() {
        new Thread() {
            @Override
            public void run() {
                if (IPAddress == null) {
                    connectToOpenSocket();
                } else {
                    connectToSocket(IPAddress);
                }
            }

            /**
             Scan the entire LAN and look for an open VolumR socket on port 8506;
             Only the last octet will be scanned through from 0 to 255;
             I.e. if the device's IP is 192.168.2.2, then this method cycles IPs from 192.168.2.0/255 to see if there's any socket open and connect to it if so.
             */
            private void connectToOpenSocket() {
                shortIPAddress = IPRetriever.getShorterIP(context);

                for (int i = 0; i <= 255; i++) {
                    final String fullIPAddress = shortIPAddress + i;
                    Log.e("Connecting to", fullIPAddress);

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            connectToSocket(fullIPAddress);
                        }
                    }.start();
                }
            }
        }.start();
    }

    public void disconnectFromPc() {
        sendMessageToPc(STOP_SERVER);
    }

    private void connectToSocket(String fullIPAddress) {
        try {
            socket = new Socket(fullIPAddress, 8506);
            if (socket != null) {
                IPAddress = fullIPAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToPc(String msg) {
        try {
            byte[] message = msg.getBytes();
            if (socket != null) {
                socket.getOutputStream().write(message);
                onConnectionListener.onMessageSend();
            } else {
                onConnectionListener.onNoConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reconnectToPc() {
        if (socket != null) {
            try {
                if (socket.isConnected()) {
                    socket.close();
                    connectToPc();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnConnectionListener {
        void onMessageSend();
        void onNoConnection();
    }

}
