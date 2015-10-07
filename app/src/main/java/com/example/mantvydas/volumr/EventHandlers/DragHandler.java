package com.example.mantvydas.volumr.EventHandlers;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mantvydas on 10/6/2015.
 */
public class DragHandler implements View.OnTouchListener {
    private OnDragListener onDragListener;
    private float y1View, y2View, y1, y2, dy, x1, x2, dx, yCurrent, xCurrent;
    private View viewToTranslate;

    public DragHandler(View viewToTranslate, OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
        this.viewToTranslate = viewToTranslate;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                y1View = viewToTranslate.getY();
                y1 = event.getRawY();
                viewToTranslate.setX(event.getRawX() - viewToTranslate.getWidth()/2);
//                viewToTranslate.setY(event.getRawY() - viewToTranslate.getHeight()/2);
                onDragListener.onOneFingerDown();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //calc difference between the coordinates where the view was tapped and where the touch coordinates are when dragging
                yCurrent = event.getRawY();
                dy = Math.abs(yCurrent - y1);

                //calculate new view's position
                if (yCurrent > y1) {
                    y2View = y1View + dy;
                } else {
                    y2View = y1View - dy;
                }

                //move view to the new position;
                if (y2View >= 0) {
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

    public interface OnDragListener {
        void onYChanged(float y);
        void onOneFingerDown();
        void onOneFingerUp();
    }
}
