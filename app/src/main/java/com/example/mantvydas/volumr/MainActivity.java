package com.example.mantvydas.volumr;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.mantvydas.volumr.EventHandlers.DragHandler;

public class MainActivity extends AppCompatActivity {
    private ImageButton volumeController;
    private TextView connectivityLabel;
    private DragHandler dragHandler;
    private ObjectAnimator objectAnimator = new ObjectAnimator();
    private final float scaleStart = 0.3f, scaleFinish = 1, scaleGone = 0;
    private TextView volumeLevel;
    private ObjectAnimator animPulsateY, animPulsateX, rotationAnimation, scaleYAnimation, scaleXAnimation;
    private String previousMessage;
    private ServerConnection server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volumeController = (ImageButton) findViewById(R.id.volumeControllerBtn);
        connectivityLabel = (TextView) findViewById(R.id.connectivityLabel);
        connectivityLabel.setText("VolumR server should be running on " + IPRetriever.getShorterIP(getBaseContext()) + "X");
        server = new ServerConnection(this);

        setFonts();
        setVolumeDragHandler();
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
            public void onYChanged(float y) {
                setVolume(y);
            }

            @Override
            public void onOneFingerUp() {
                collapseVolumeController();
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

    private void setVolume(float y) {
        float volumeFloat = 100 - (y / (dragHandler.getScreenInformation().y - volumeController.getHeight())) * 100;
        String volumeRounded = Integer.toString(Math.round(volumeFloat));
        volumeLevel.setText(volumeRounded);

        if (previousMessage != volumeRounded) {
            server.sendMessageToPc(volumeRounded);
        }
        previousMessage = volumeRounded;
    }


    public void showConnectivityLabel() {
        connectivityLabel.setVisibility(View.VISIBLE);
    }

    public void collapseConnectivityLabel() {
        if (connectivityLabel.getVisibility() == View.VISIBLE) {
            connectivityLabel.setVisibility(View.GONE);
        }
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

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        disconnectFromPc();
        server.disconnectFromPc();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        reconnectToPc();
        server.reconnectToPc();
    }
}
