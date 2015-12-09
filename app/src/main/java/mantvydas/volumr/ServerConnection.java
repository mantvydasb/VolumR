package mantvydas.volumr;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by mantvydas on 10/13/2015.
 */
public class ServerConnection {
    private final String STOP_SERVER = "STOP_SERVER";
//    private Socket socket;
    private SSLSocket socket;
    private String shortIPAddress;
    private String IPAddress = "10.53.12.78";
    private Context context;
    private OnConnectionListener onConnectionListener;
    static ServerConnection serverConnection;

    InputStream caInput = null;
    Certificate ca = null;
    CertificateFactory cf = null;
    KeyStore keyStore = null;
    TrustManager trustManager[];


    /**
     * Connects to a a server - user's PC which is on the same network as user's Android device
     * @param onConnectionListener - listener for connection related events
     * @param context required retrieving device's IP address
     */
    public ServerConnection(OnConnectionListener onConnectionListener, Context context) {
        this.onConnectionListener = onConnectionListener;
        this.context = context;
        connectToPc();
        this.serverConnection = this;
    }

    public boolean isConnected() {
        return (socket != null) ? socket.isConnected() : false;
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
                shortIPAddress = WifiIPRetriever.getShorterIP(context);

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

    private void connectToSocket(final String fullIPAddress) {
        final int dstPort = 8506;
//            new Socket(fullIPAddress, dstPort);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    test();
//                    SocketFactory socketFactory = SSLSocketFactory.getDefault();
//                    SocketFactory socketFactory;

                    SSLSocketFactory socketFactory = (SSLSocketFactory) SSLCertificateSocketFactory.getDefault();


//                    socketFactory.setTrustManagers(trustManager);

                    socket = (SSLSocket) socketFactory.createSocket(fullIPAddress, dstPort);
                    socket.setUseClientMode(true);
                    SSLSession sslSession = socket.getSession();
                    Certificate[] certificates = sslSession.getLocalCertificates();

                    sendMessageToPc("SSL message testing");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();

        if (socket != null) {
            IPAddress = fullIPAddress;
        }
    }

    public void sendMessageToPc(final String msg) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    byte[] message = msg.getBytes();
                    if (socket != null) {
                        socket.getOutputStream().write(message);
                        socket.getOutputStream().flush();
                        onConnectionListener.onMessageSend();
                    } else {
                        onConnectionListener.onNoConnection();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();


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

    private void test() {
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (java.security.cert.CertificateException e) {
            e.printStackTrace();
        }

        try {
            InputStream open = context.getAssets().open("volumr.crt");
            caInput = new BufferedInputStream(open);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }

        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } catch (java.security.cert.CertificateException e) {
            e.printStackTrace();
//            caInput.close();
        }


        try {
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (java.security.cert.CertificateException e) {
            e.printStackTrace();
        }

        SSLContext context = null;

        try {
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            trustManager = tmf.getTrustManagers();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }


    public interface OnConnectionListener {
        void onMessageSend();
        void onNoConnection();
    }

}
