package mantvydas.volumr.EventHandlers;

import android.app.Activity;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mantvydas on 10/6/2015.
 */
public class DragHandler implements View.OnTouchListener {
    private OnDragListener onDragListener;
    private float y1View, y2View, x1View, x2View, yTouchedPointer, xTouchedPointer, dx, dy, yCurrentPointer, xCurrentPointer;
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
        int fingersCount = event.getPointerCount();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN: {
                onDragListener.onMultipleFingersDown();

                getPointerCoordinates(event);
                viewToTranslate.setX(xTouchedPointer - viewToTranslate.getWidth() / 2);
                viewToTranslate.setY(yTouchedPointer - viewToTranslate.getHeight() / 2);

                y1View = viewToTranslate.getY();
                x1View = viewToTranslate.getX();

                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                onDragListener.onMultipleFingersUp();
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                //move vol controller to where the finger is touching the screen;
                yTouchedPointer = event.getRawY();
                xTouchedPointer = event.getRawX();
                viewToTranslate.setX(xTouchedPointer - viewToTranslate.getWidth() / 2);
                viewToTranslate.setY(yTouchedPointer - 70 - viewToTranslate.getHeight() / 2);

                y1View = viewToTranslate.getY();
                x1View = viewToTranslate.getX();

                onDragListener.onOneFingerDown();
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                //calc difference between the coordinates where the view was tapped and where the touch coordinates are when dragging
                if (fingersCount > 1) {
                    xCurrentPointer = (Math.abs(MotionEventCompat.getX(event, 0) + MotionEventCompat.getX(event, 1))) / 2;
                    yCurrentPointer = (Math.abs(MotionEventCompat.getY(event, 0) + MotionEventCompat.getY(event, 1))) / 2;
                    onDragListener.onMultipleFingersMove();
                } else if (fingersCount == 0) {
                    xCurrentPointer = event.getRawX();
                    yCurrentPointer = event.getRawY();
                }

                //calculate the distance the object should move from its original position + the distance the fingers dragged;
                dy = Math.abs(yCurrentPointer - yTouchedPointer);
                dx = Math.abs(xCurrentPointer - xTouchedPointer);

                //calculate new view's Y
                if (yCurrentPointer > yTouchedPointer) {
                    y2View = y1View + dy;
                } else {
                    y2View = y1View - dy;
                }

                //calculate new view's X
                if (xCurrentPointer > xTouchedPointer) {
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

    private void getPointerCoordinates(MotionEvent event) {
        int x1, x2, y1, y2;
        x1 = (int) MotionEventCompat.getX(event, 0);
        y1 = (int) MotionEventCompat.getY(event, 0);

        x2 = (int) MotionEventCompat.getX(event, 1);
        y2 = (int) MotionEventCompat.getY(event, 1);

        xTouchedPointer = (x1 + x2) / 2;
        yTouchedPointer = (y1 + y2) / 2;
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
        void onMultipleFingersDown();
        void onMultipleFingersUp();
        void onMultipleFingersMove();
    }
}
