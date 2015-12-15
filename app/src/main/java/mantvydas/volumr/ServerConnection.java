package mantvydas.volumr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


/**
 * Created by mantvydas on 10/13/2015.
 */
public class ServerConnection {
    private final String STOP_SERVER = "STOP_SERVER";
    private SSLSocket socket;
    private String shortIPAddress;
    private String IPAddress = null;
    final int PORT = 8506;
    final String CERTIFICATE = "server.crt";
    private Context context;
    private OnConnectionListener onConnectionListener;
    static ServerConnection serverConnection;

    /**
     * Connects to a a server - user's PC which is on the same network as user's Android device
     * @param onConnectionListener - listener for connection related events
     * @param context required retrieving device's IP address
     */
    public ServerConnection(OnConnectionListener onConnectionListener, Context context) {
        this.onConnectionListener = onConnectionListener;
        this.context = context;
        attemptToConnect();
        this.serverConnection = this;
    }

    public boolean isConnected() {
        return (socket != null) ? socket.isConnected() : false;
    }

    public void attemptToConnect() {
        new Thread() {
            @Override
            public void run() {
                if (IPAddress == null) {
                    connectToOpenSocket();
                } else {
                    connectToSocket(IPAddress);
                }
            }
        }.start();
    }

    /**
     Scan the entire LAN and look for an open VolumR socket on port 8506;
     Only the last octet will be scanned through from 0 to 255;
     I.e. if the device's IP is 192.168.2.2, then this method cycles IPs from 192.168.2.1-255 to see if there's any socket open and connect to it if so.
     */
    private void connectToOpenSocket() {
        shortIPAddress = WifiIPRetriever.getShorterIP(context);

        for (int i = 1; i <= 255; i++) {
            final String fullIPAddress = shortIPAddress + i;
            Log.e("Connecting to", fullIPAddress);
            connectToSocket(fullIPAddress);
        }
    }

    public void disconnectFromPc() {
        sendMessageToPc(STOP_SERVER);
    }

    private void connectToSocket(final String fullIPAddress) {
        try {
            SSLSocketFactory socketFactory = new TLSSocketFactory().createSSLSocketFactory(context, CERTIFICATE);
            socket = (SSLSocket) socketFactory.createSocket(fullIPAddress, PORT);
            socket.setUseClientMode(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (socket != null) {
            IPAddress = fullIPAddress;
        }
    }

    public void sendMessageToPc(final String msg) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    byte[] message = msg.getBytes();
                    if (socket != null) {
                        socket.getOutputStream().write(message);
                        socket.getOutputStream().flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                 return discardSocketIfRequired();
            }

            private boolean discardSocketIfRequired() {
                if (msg == STOP_SERVER) {
                    socket = null;
                    IPAddress = null;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSocketAlive) {
                super.onPostExecute(isSocketAlive);

                if (isSocketAlive) {
                    onConnectionListener.onMessageSend();
                } else {
                    onConnectionListener.onConnectionLost();
                }
            }
        }.execute();
    }

    public void reconnectToPc() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (socket == null) {
                    attemptToConnect();
                }
                return null;
            }
        }.execute();
    }


    public interface OnConnectionListener {
        void onMessageSend();
        void onConnectionLost();
    }
}
