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
    private float y1View, y2View, x1View, x2View, yTouchedPointer, xTouchedPointer, dx, dy, yCurrentPointer, yOldPointer, xCurrentPointer;
    private View viewToTranslate;
    private Point screenSize = new Point();
    private Activity activity;

    public static class Direction {
        public final static int LEFT = 0;
        public final static int RIGHT = 1;
    }

    public DragHandler(View viewToTranslate, Activity activity, OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
        this.viewToTranslate = viewToTranslate;
        this.activity = activity;
        getScreenSize();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        activity.onTouchEvent(event);
        int fingersCount = event.getPointerCount();

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_POINTER_DOWN: {
                onDragListener.onMultipleFingersDown();
                getAverageTouchPointerCoordinates(event);
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
                Log.e("fingers", String.valueOf(fingersCount));

                //calc difference between the coordinates where the view was tapped and where the touch coordinates are when dragging
                if (fingersCount > 1) {
                    xCurrentPointer = (MotionEventCompat.getX(event, 0) + MotionEventCompat.getX(event, 1)) / 2;
                    yCurrentPointer = (MotionEventCompat.getY(event, 0) + MotionEventCompat.getY(event, 1)) / 2;

                    if (xCurrentPointer > xTouchedPointer) {
                        onDragListener.onMultipleFingersMove(Direction.RIGHT);
                    } else {
                        onDragListener.onMultipleFingersMove(Direction.LEFT);
                    }
                } else if (fingersCount == 1) {
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
                    onDragListener.onYChanged(y2View, fingersCount);
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

    private void getAverageTouchPointerCoordinates(MotionEvent event) {
        float x1, x2, y1, y2;
        x1 = MotionEventCompat.getX(event, 0);
        y1 = MotionEventCompat.getY(event, 0);
        x2 = MotionEventCompat.getX(event, 1);
        y2 = MotionEventCompat.getY(event, 1);

        xTouchedPointer = (x1 + x2) / 2;
        yTouchedPointer = (y1 + y2) / 2;
    }

    public Point getScreenSize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getSize(screenSize);
        return screenSize;
    }

    public interface OnDragListener {
        void onYChanged(float y, int numberOfFingers);
        void onOneFingerDown();
        void onOneFingerUp();
        void onMultipleFingersDown();
        void onMultipleFingersUp();
        void onMultipleFingersMove(float x, float y);
        void onMultipleFingersMove(int direction);
    }
}
