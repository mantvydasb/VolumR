package mantvydas.volumr;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import mantvydas.volumr.Animators.VolumeControllerAnimator;
import mantvydas.volumr.EventHandlers.BackgroundVolumeChanger;
import mantvydas.volumr.EventHandlers.DragHandler;

public class MainActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        ServerConnection.OnConnectionListener {
    private ImageButton volumeController;
    private TextView connectivityLabel;
    private DragHandler dragHandler;
    private TextView volumeLevel;
    private GestureDetectorCompat gestureDetector;
    private ServerConnection server;

    //commands that can be sent to the server;
    private final String VK_LEFT = "left:1;";
    private final String VK_RIGHT = "right:1;";
    private final String VK_SPACE = "space:1;";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volumeController = (ImageButton) findViewById(R.id.volumeControllerBtn);
        connectivityLabel = (TextView) findViewById(R.id.connectivityLabel);
        setConnectivityLabel();
        server = new ServerConnection(this, this);

        setFonts();
        setVolumeDragHandler();
//        addPhysicalVolumeChangeListener();
        startBackgroundVolumeChangerService();
        AnalyticsLogger.logActivity("MainActivity", getApplication());
        gestureDetector = new GestureDetectorCompat(getApplicationContext(), this);
        gestureDetector.setOnDoubleTapListener(this);
    }

    private void startBackgroundVolumeChangerService() {
        startService(new Intent(this, BackgroundVolumeChanger.class));
    }

    private void addPhysicalVolumeChangeListener() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("VOLUME_CHANGED");

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                float volume = (float) intent.getExtras().get("VOLUME");
                changeVolumeWithPhysicalKeys(Math.round(volume));
            }
        }, intentFilter);
    }

    private void setConnectivityLabel() {
        final String shorterIP = WifiIPRetriever.getShorterIP(getBaseContext());
        String text = null;

        if (shorterIP != null) {
            text = getResources().getString(R.string.connectivityShouldBeRunningOn) + " " + shorterIP + "X";
        } else {
            text = getResources().getString(R.string.connectivityNoServer);
        }
        connectivityLabel.setText(text);
    }

    private void setFonts() {
        Typeface tungsten = Typeface.createFromAsset(getAssets(), "fonts/tungsten_light.otf");
        volumeLevel = (TextView) findViewById(R.id.volumeLevelLabel);
        volumeLevel.setTypeface(tungsten);
        connectivityLabel.setTypeface(tungsten);
    }

    private void setVolumeDragHandler() {
        dragHandler = new DragHandler(volumeController, this, new DragHandler.OnDragListener() {
            boolean canChangeVolume = true;

            //used to decrease sensitivity of the left/right commands;
            int eventCounter = 0;

            @Override
            public void onYChanged(float y, int numberOfFingers) {
                if (numberOfFingers == 1 && canChangeVolume == true) {
                    changeVolume(y);
                }
            }

            @Override
            public void onOneFingerUp() {
                collapseVolumeController();
            }

            @Override
            public void onMultipleFingersDown() {
                volumeController.setImageResource(R.drawable.volume_controller_two_fingers);
                setCanChangeVolume(false);
            }

            @Override
            public void onMultipleFingersUp() {
                volumeController.setImageResource(R.drawable.volume_controller);
                setCanChangeVolume(true);
            }

            private void setCanChangeVolume(final boolean canChangeOrNot) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        canChangeVolume = canChangeOrNot;
                    }
                }, 500);
            }

            @Override
            public void onMultipleFingersMove(float x, float y) {}

            @Override
            public void onMultipleFingersMove(int direction) {
                if (eventCounter > 7) {
                    switch (direction) {
                        case DragHandler.Direction.LEFT: {
                            seekBackward();
                            break;
                        }
                        case DragHandler.Direction.RIGHT: {
                            seekForward();
                            break;
                        }
                    }
                    eventCounter = 0;
                }
                eventCounter++;
            }

            @Override
            public void onOneFingerDown() {
                expandVolumeController();
            }
        });

        findViewById(R.id.mainActivity).setOnTouchListener(dragHandler);
    }

    private void expandVolumeController() {
        VolumeControllerAnimator.expandVolumeController(volumeController);
    }

    private void collapseVolumeController() {
        VolumeControllerAnimator.collapseVolumeController();
    }


    //SERVER COMMANDS
    public void changeVolume(float y) {
        float volumeFloat = 100 - (y / (dragHandler.getScreenSize().y - volumeController.getHeight())) * 100;
        String volume = Integer.toString(Math.round(volumeFloat));
        volumeLevel.setText(volume);
        final String message = "volume:" + volume + ";";

        Log.e("changeVolume: ", message);


        new Thread() {
            @Override
            public void run() {
                super.run();
                sendMessageToPc(message);
            }
        }.run();

    }

    private void seekForward() {
        sendMessageToPc(VK_RIGHT);
    }

    private void seekBackward() {
        sendMessageToPc(VK_LEFT);
    }

    private void pressSpace() {
        /*
            Used to send a space bar press, which usually pauses/resumes the video player;
         */
        sendMessageToPc(VK_SPACE);
    }

    private void sendMessageToPc(final String message) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (ServerConnection.serverConnection.isConnected()) {
                    ServerConnection.serverConnection.sendMessageToPc(message);
                } else {
                    setConnectivityLabel();
                }
            }
        }.run();
    }

    public void changeVolumeWithPhysicalKeys(int volume) {
        if (volume <= 100) {
            sendMessageToPc(String.valueOf(volume));
        }
    }



    public void showConnectivityLabel() {
        connectivityLabel.setVisibility(View.VISIBLE);
    }

    public void collapseConnectivityLabel() {
        if (connectivityLabel.getVisibility() == View.VISIBLE) {
            connectivityLabel.setVisibility(View.GONE);
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        pressSpace();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }




    @Override
    public void onNoConnection() {
        showConnectivityLabel();
    }

    @Override
    public void onMessageSend() {
        collapseConnectivityLabel();
    }




    @Override
    protected void onStop() {
        super.onStop();
        server.disconnectFromPc();
    }

    @Override
    protected void onResume() {
        super.onResume();
        server.reconnectToPc();
    }
}
