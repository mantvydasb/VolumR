package com.example.mantvydas.volumr.EventHandlers;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mantvydas on 10/6/2015.
 */
public class DragHandler implements View.OnTouchListener {
    private OnDragListener onDragListener;
    private float y1View, y2View, x1View, x2View, yTouched, dy, xTouched, dx, yCurrent, xCurrent;
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
                yTouched = event.getRawY();
                xTouched = event.getRawX();
                viewToTranslate.setX(xTouched - viewToTranslate.getWidth() / 2);
                viewToTranslate.setY(yTouched - 70 - viewToTranslate.getHeight() / 2);

                y1View = viewToTranslate.getY();
                x1View = viewToTranslate.getX();

                onDragListener.onOneFingerDown();
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                //calc difference between the coordinates where the view was tapped and where the touch coordinates are when dragging
                yCurrent = event.getRawY();
                xCurrent = event.getRawX();
                dy = Math.abs(yCurrent - yTouched);
                dx = Math.abs(xCurrent - xTouched);

                //calculate new view's Y
                if (yCurrent > yTouched) {
                    y2View = y1View + dy;
                } else {
                    y2View = y1View - dy;
                }

                //calculate new view's X
                if (xCurrent > xTouched) {
                    x2View = x1View + dx;
                } else {
                    x2View = x1View - dx;
                }

                //move view to the new position;
                if (y2View >= 0 && y2View <= screenSize.y - viewToTranslate.getHeight()) {
                    viewToTranslate.setY(y2View);
                    onDragListener.onYChanged(y2View);
                }
                viewToTranslate.setX(x2View);

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
