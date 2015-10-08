package com.example.mantvydas.volumr;

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
    private DragHandler dragHandler;
    private ObjectAnimator objectAnimator = new ObjectAnimator();
    ObjectAnimator rotate = new ObjectAnimator();
    float scaleStart = 0.3f, scaleFinish = 1, scaleGone = 0;
    private TextView volumeLevel, volumeLevel2;
    private ObjectAnimator animPulsateY, animPulsateX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volumeController = (ImageButton) findViewById(R.id.volume_controller);
        setVolumeLevelFont();
        setVolumeDragHandler();
        collapseVolumeController();
        startRotatingVolumeController();
    }

    private void setVolumeLevelFont() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/tungsten_light.otf");
        volumeLevel = (TextView) findViewById(R.id.volumeLevel);
        volumeLevel2 = (TextView) findViewById(R.id.volumeLevel2);
        volumeLevel.setTypeface(typeface);
        volumeLevel2.setTypeface(typeface);
    }

    private void setVolumeDragHandler() {
        dragHandler = new DragHandler(volumeController, this, new DragHandler.OnDragListener() {
            @Override
            public void onYChanged(float y) {
                setVolume(y);
                volumeController.setRotation((y/dragHandler.getScreenInformation().y * 360) * -1);
            }

            @Override
            public void onOneFingerUp() {
                collapseVolumeController();
            }

            @Override
            public void onOneFingerDown() {
                startPulsatingVolumeController();
            }
        });

        findViewById(R.id.mainActivity).setOnTouchListener(dragHandler);
    }

    private void startPulsatingVolumeController() {
        volumeController.setVisibility(View.VISIBLE);
        objectAnimator.ofFloat(volumeController, "scaleX", scaleStart, scaleFinish).start();
        objectAnimator.ofFloat(volumeController, "scaleY", scaleStart, scaleFinish).start();

        float scaleUp = 1.03f;
        animPulsateX = ObjectAnimator.ofFloat(volumeController, "scaleX", scaleFinish, scaleUp);
        animPulsateX.setRepeatCount(ValueAnimator.INFINITE);
        int duration = 900;
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
        ObjectAnimator anim = ObjectAnimator.ofFloat(volumeController, "rotation", 0, 360);
        anim.setDuration(15000);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
    }

    private void setVolume(float y) {
        float grade = 100 - (y / (dragHandler.getScreenInformation().y - volumeController.getHeight())) * 100;
        volumeLevel.setText(Integer.toString(Math.round(grade)));
        volumeLevel2.setText(Integer.toString(Math.round(grade)));
    }

    private void collapseVolumeController() {
        try {
            objectAnimator.ofFloat(volumeController, "scaleY", scaleFinish, scaleGone).start();
            objectAnimator.ofFloat(volumeController, "scaleX", scaleFinish, scaleGone).start();
            stopPulsatingVolumeController();
//            volumeController.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopPulsatingVolumeController() {
        animPulsateX.cancel();
        animPulsateY.cancel();
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
}
