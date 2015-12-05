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

import mantvydas.volumr.EventHandlers.BackgroundVolumeChanger;
import mantvydas.volumr.EventHandlers.DragHandler;

public class MainActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        ServerConnection.OnConnectionListener {

    private ImageButton volumeController;
    private TextView connectivityLabel;
    private DragHandler dragHandler;
    private ObjectAnimator objectAnimator = new ObjectAnimator();
    private final float scaleStart = 0.3f, scaleFinish = 1, scaleGone = 0;
    private TextView volumeLevel;
    private ObjectAnimator animPulsateY, animPulsateX, rotationAnimation, scaleYAnimation, scaleXAnimation;
    private String previousMessage;
    private GestureDetectorCompat gestureDetector;
    private ServerConnection server;

    private final String VK_LEFT = "seek:0";
    private final String VK_RIGHT = "seek:1";
    private final String VK_SPACE = "space:1";

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
            @Override
            public void onYChanged(float y, int numberOfFingers) {
                if (numberOfFingers == 1) {
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
            }

            @Override
            public void onMultipleFingersUp() {
                volumeController.setImageResource(R.drawable.volume_controller);
            }

            @Override
            public void onMultipleFingersMove(float x, float y) {}

            @Override
            public void onMultipleFingersMove(int direction) {
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
            }

            @Override
            public void onOneFingerDown() {
                expandVolumeController();
            }
        });

        findViewById(R.id.mainActivity).setOnTouchListener(dragHandler);
    }

    private void expandVolumeController() {
        startPulsatingVolumeController();
        startRotatingVolumeController();
    }

    private void collapseVolumeController() {
        try {
            int duration = 80;
            scaleYAnimation = ObjectAnimator.ofFloat(volumeController, "scaleY", scaleFinish, scaleGone);
            scaleXAnimation = ObjectAnimator.ofFloat(volumeController, "scaleX", scaleFinish, scaleGone);
            scaleYAnimation.setDuration(duration);
            scaleXAnimation.setDuration(duration);
            scaleYAnimation.start();
            scaleXAnimation.start();

            scaleYAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    volumeController.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPulsatingVolumeController() {
        float scaleUp = 1.03f;
        int duration = 900;

        objectAnimator.ofFloat(volumeController, "scaleX", scaleStart, scaleFinish).setDuration(150).start();
        objectAnimator.ofFloat(volumeController, "scaleY", scaleStart, scaleFinish).setDuration(150).start();

        animPulsateX = ObjectAnimator.ofFloat(volumeController, "scaleX", scaleFinish, scaleUp);
        animPulsateX.setRepeatCount(ValueAnimator.INFINITE);
        animPulsateX.setDuration(duration);
        animPulsateX.setRepeatMode(ValueAnimator.REVERSE);
        animPulsateX.start();

        animPulsateY = ObjectAnimator.ofFloat(volumeController, "scaleY", scaleFinish, scaleUp);
        animPulsateY.setRepeatCount(ValueAnimator.INFINITE);
        animPulsateY.setRepeatMode(ValueAnimator.REVERSE);
        animPulsateY.setDuration(duration);
        animPulsateY.start();
    }

    private void startRotatingVolumeController() {
        volumeController.setVisibility(View.VISIBLE);
        rotationAnimation = ObjectAnimator.ofFloat(volumeController, "rotation", 0, 360);
        rotationAnimation.setDuration(15000);
        rotationAnimation.setInterpolator(new LinearInterpolator());
        rotationAnimation.setRepeatMode(ValueAnimator.RESTART);
        rotationAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimation.start();
    }

    public void changeVolume(float y) {
        float volumeFloat = 100 - (y / (dragHandler.getScreenInformation().y - volumeController.getHeight())) * 100;
        String volume = Integer.toString(Math.round(volumeFloat));
        volumeLevel.setText(volume);
        String message = "volume:" + volume + ";";

        Log.e("changeVolume: ", message);
        sendMessageToPc(message);
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

    private void sendMessageToPc(String message) {
        if (ServerConnection.serverConnection.isConnected()) {
            if (previousMessage != message) {
                ServerConnection.serverConnection.sendMessageToPc(message);
            }
            previousMessage = message;
        } else {
            setConnectivityLabel();
        }
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
//        server.disconnectFromPc();
//        ServerConnection.serverConnection.disconnectFromPc();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        server.reconnectToPc();
//        ServerConnection.serverConnection.reconnectToPc();
    }
}
