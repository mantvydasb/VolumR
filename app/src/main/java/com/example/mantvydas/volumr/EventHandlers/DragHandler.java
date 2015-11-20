package com.example.mantvydas.volumr.EventHandlers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mantvydas on 10/6/2015.
 */
public class DragHandler implements View.OnTouchListener {
    private OnDragListener onDragListener;
    private float y1View, y2View, x1View, y1, dy, x1, dx, yCurrent, xCurrent;
    private View viewToTranslate;
    private Point screenSize = new Point();
    private Activity activity;

    public DragHandler(View viewToTranslate, Activity activity, OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
        this.viewToTranslate = viewToTranslate;
        this.activity = activity;
        getScreenInformation();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                //move vol controller to where the finger is touching the screen;
                y1 = event.getRawY();
                x1 = event.getRawX();
                viewToTranslate.setX(x1 - viewToTranslate.getWidth() / 2);
                viewToTranslate.setY(y1 - 70 - viewToTranslate.getHeight() / 2);
                y1View = viewToTranslate.getY();
                x1View = viewToTranslate.getX();
                onDragListener.onOneFingerDown();

                break;
            }
            case MotionEvent.ACTION_MOVE: {

                //calc difference between the coordinates where the view was tapped and where the touch coordinates are when dragging
                yCurrent = event.getRawY();
                xCurrent = event.getRawX();
                dy = Math.abs(yCurrent - y1);
                dx = Math.abs(xCurrent - x1);

                viewToTranslate.setX(xCurrent + dx);

                //calculate new view's position
                if (yCurrent > y1) {
                    y2View = y1View + dy;
                } else {
                    y2View = y1View - dy;
                }

                //move view to the new position;
                if (y2View >= 0 && y2View <= screenSize.y - viewToTranslate.getHeight()) {
                    viewToTranslate.setY(y2View);
                    onDragListener.onYChanged(y2View);
                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                onDragListener.onOneFingerUp();
            }
        }

        return true;
    }

    public Point getScreenInformation() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getSize(screenSize);
        return screenSize;
    }

    public interface OnDragListener {
        void onYChanged(float y);
        void onOneFingerDown();
        void onOneFingerUp();
    }
}
