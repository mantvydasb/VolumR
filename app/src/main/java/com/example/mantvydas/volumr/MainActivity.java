package com.example.mantvydas.volumr;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mantvydas.volumr.EventHandlers.DragHandler;

public class MainActivity extends AppCompatActivity {
    private ImageButton volumeController;
    private DragHandler dragHandler;
    private ObjectAnimator objectAnimator = new ObjectAnimator();
    float scaleStart = 0.3f, scaleFinish = 1, scaleGone = 0;
    private TextView volumeLevel, volumeLevel2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volumeController = (ImageButton) findViewById(R.id.volume_controller);
        setVolumeLevelFont();
        setVolumeDragHandler();
        collapseVolumeController();
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
                volumeLevel.setY(y + volumeLevel.getHeight()/2);
            }

            @Override
            public void onOneFingerUp() {
                collapseVolumeController();
            }

            @Override
            public void onOneFingerDown() {
                objectAnimator.ofFloat(volumeController, "scaleX", scaleStart, scaleFinish).start();
                objectAnimator.ofFloat(volumeController, "scaleY", scaleStart, scaleFinish).start();
//                volumeLevel.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.mainActivity).setOnTouchListener(dragHandler);
    }

    private void setVolume(float y) {
        float grade = 100 - (y / (dragHandler.getScreenInformation().y - volumeController.getHeight())) * 100;
        volumeLevel.setText(Integer.toString(Math.round(grade)));
        volumeLevel2.setText(Integer.toString(Math.round(grade)));
    }

    private void collapseVolumeController() {
        objectAnimator.ofFloat(volumeController, "scaleY", scaleFinish, scaleGone).start();
        objectAnimator.ofFloat(volumeController, "scaleX", scaleFinish, scaleGone).start();
//        volumeLevel.setVisibility(View.GONE);
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
