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
    private float y1View, y2View, x1View, x2View, yTouched, dy, xTouched, dx, yCurrent, xCurrent, xCurrentMulti, yCurrentMulti;
    private View viewToTranslate;
    private Point screenSize = new Point();
    private Activity activity;
    private int xt1 = 0, xt2 = 0, yt1 = 0, yt2 = 0;

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
                viewToTranslate.setX(xTouched - viewToTranslate.getWidth() / 2);
                viewToTranslate.setY(yTouched - viewToTranslate.getHeight() / 2);

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
                Log.e("fingers", String.valueOf(fingersCount));

                //calc difference between the coordinates where the view was tapped and where the touch coordinates are when dragging
                if (fingersCount > 1) {
                    onDragListener.onMultipleFingersMove();
                    xCurrent = (Math.abs(MotionEventCompat.getX(event, 0) + MotionEventCompat.getX(event, 1))) / 2;
                    yCurrent = (Math.abs(MotionEventCompat.getY(event, 0) + MotionEventCompat.getY(event, 1))) / 2;
                } else if (fingersCount == 0) {
                    xCurrent = event.getRawX();
                    yCurrent = event.getRawY();
                }

                //calculate the distance the object should move from its original position + the distance the fingers dragged;
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

    private void getPointerCoordinates(MotionEvent event) {
        xt1 = (int) MotionEventCompat.getX(event, 0);
        yt1 = (int) MotionEventCompat.getY(event, 0);

        xt2 = (int) MotionEventCompat.getX(event, 1);
        yt2 = (int) MotionEventCompat.getY(event, 1);

        xTouched = (xt1 + xt2) / 2;
        yTouched = (yt1 + yt2) / 2;
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
