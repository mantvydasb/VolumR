package com.example.mantvydas.volumr;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
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
    private TextView volumeLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/tungsten_light.otf");
        volumeController = (ImageButton) findViewById(R.id.volume_controller);
        volumeLevel = (TextView) findViewById(R.id.volumeLevel);
        volumeLevel.setTypeface(typeface);
        collapseVolumeController();

        dragHandler = new DragHandler(volumeController, this, new DragHandler.OnDragListener() {
            @Override
            public void onYChanged(float y) {
                volumeLevel.setText(Integer.toString(Math.round(y)));
            }

            @Override
            public void onOneFingerUp() {
                collapseVolumeController();
            }

            @Override
            public void onOneFingerDown() {
                objectAnimator.ofFloat(volumeController, "scaleX", scaleStart, scaleFinish).start();
                objectAnimator.ofFloat(volumeController, "scaleY", scaleStart, scaleFinish).start();
//
//                ObjectAnimator objectAnimator1 = new ObjectAnimator();
//                objectAnimator.ofFloat(volumeController, "rotate", 0, 360).start();
//                objectAnimator1.start();
            }
        });

        findViewById(R.id.mainActivity).setOnTouchListener(dragHandler);
    }

    private void collapseVolumeController() {
        objectAnimator.ofFloat(volumeController, "scaleY", scaleFinish, scaleGone).start();
        objectAnimator.ofFloat(volumeController, "scaleX", scaleFinish, scaleGone).start();
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
