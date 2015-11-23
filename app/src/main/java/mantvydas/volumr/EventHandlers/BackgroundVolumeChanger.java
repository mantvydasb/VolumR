package mantvydas.volumr.EventHandlers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import mantvydas.volumr.MainActivity;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by mantvydas on 11/20/2015.
 */
public class BackgroundVolumeChanger extends Service {
    public BackgroundVolumeChanger() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        addPhysicalVolumeChangeListener(intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    private void addPhysicalVolumeChangeListener(IntentFilter intentFilter) {
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int volume = (int) intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE") + 1;
                float newVolume = ((float)volume / 8) * 100;
                broadcastVolumeChanges(intent, newVolume);
            }

            private void broadcastVolumeChanges(Intent intent, float newVolume) {
                intent.setAction("VOLUME_CHANGED");
                intent.putExtra("VOLUME", newVolume);
                sendBroadcast(intent);
            }
        }, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("on Bind", "onStartCommand: ");
        return null;
    }
}
