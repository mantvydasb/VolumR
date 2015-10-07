package com.example.mantvydas.volumr;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.mantvydas.volumr.EventHandlers.DragHandler;

public class MainActivity extends AppCompatActivity {
    ImageButton volumeController;
    DragHandler dragHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ObjectAnimator objectAnimator = new ObjectAnimator();
        volumeController = (ImageButton) findViewById(R.id.volume_controller);

        dragHandler = new DragHandler(volumeController, this, new DragHandler.OnDragListener() {
            float scaleStart = 0.3f, scaleFinish = 1, scaleGone = 0;

            @Override
            public void onYChanged(float y) {
                Log.e("vykst", Float.toString(y));
            }

            @Override
            public void onOneFingerUp() {
                objectAnimator.ofFloat(volumeController, "scaleY", scaleFinish, scaleGone).start();
                objectAnimator.ofFloat(volumeController, "scaleX", scaleFinish, scaleGone).start();
            }

            @Override
            public void onOneFingerDown() {
                ObjectAnimator.ofFloat(volumeController, "scaleX", scaleStart, scaleFinish).start();
                ObjectAnimator.ofFloat(volumeController, "scaleY", scaleStart, scaleFinish).start();
            }
        });

        findViewById(R.id.mainActivity).setOnTouchListener(dragHandler);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
