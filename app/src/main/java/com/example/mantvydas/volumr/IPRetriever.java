package com.example.mantvydas.volumr;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * Created by mantvydas on 10/13/2015.
 */
public class IPRetriever {

    public static String getShorterIP(Context context) {
        String IPAddress = getIPAddress(context);
        return stripLastIPOctet(IPAddress);
    }

    @Nullable
    public static String getIPAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String IPAddress;
        try {
            IPAddress = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            IPAddress = null;
        }

        return IPAddress;
    }


    private static String stripLastIPOctet(String IPAddress) {
        int lastDotIndex = IPAddress.lastIndexOf(".");
        return IPAddress.substring(0, lastDotIndex) + ".";
    }

}
