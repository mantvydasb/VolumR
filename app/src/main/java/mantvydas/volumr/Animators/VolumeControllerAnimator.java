package mantvydas.volumr.Animators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import mantvydas.volumr.R;

/**
 * Created by baranauskasm on 07/12/2015.
 */
public class VolumeControllerAnimator {
    private static ObjectAnimator objectAnimator = new ObjectAnimator();
    private static ObjectAnimator animPulsateY, animPulsateX, rotationAnimation, scaleYAnimation, scaleXAnimation;
    private static final float scaleStart = 0.3f, scaleFinish = 1, scaleGone = 0;
    private static ImageButton volumeController;

    public static void
    expandVolumeController(ImageButton volumeCtrl) {
        volumeController = volumeCtrl;
        startPulsatingVolumeController();
        startRotatingVolumeController();
    }

    public static void collapseVolumeController() {
        try {
            int duration = 80;
            scaleYAnimation = ObjectAnimator.ofFloat(volumeController, "scaleY", scaleFinish, scaleGone);
            scaleXAnimation = ObjectAnimator.ofFloat(volumeController, "scaleX", scaleFinish, scaleGone);
            scaleYAnimation.setDuration(duration);
            scaleXAnimation.setDuration(duration);
            scaleYAnimation.start();
            scaleXAnimation.start();

            scaleYAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    volumeController.setVisibility(View.GONE);
                    volumeController.setImageResource(R.drawable.volume_controller);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startPulsatingVolumeController() {
        float scaleUp = 1.03f;
        int duration = 900;

        objectAnimator.ofFloat(volumeController, "scaleX", scaleStart, scaleFinish).setDuration(150).start();
        objectAnimator.ofFloat(volumeController, "scaleY", scaleStart, scaleFinish).setDuration(150).start();

        animPulsateX = ObjectAnimator.ofFloat(volumeController, "scaleX", scaleFinish, scaleUp);
        animPulsateX.setRepeatCount(ValueAnimator.INFINITE);
        animPulsateX.setDuration(duration);
        animPulsateX.setRepeatMode(ValueAnimator.REVERSE);
        animPulsateX.start();

        animPulsateY = ObjectAnimator.ofFloat(volumeController, "scaleY", scaleFinish, scaleUp);
        animPulsateY.setRepeatCount(ValueAnimator.INFINITE);
        animPulsateY.setRepeatMode(ValueAnimator.REVERSE);
        animPulsateY.setDuration(duration);
        animPulsateY.start();
    }

    private static void startRotatingVolumeController() {
        volumeController.setVisibility(View.VISIBLE);
        rotationAnimation = ObjectAnimator.ofFloat(volumeController, "rotation", 0, 360);
        rotationAnimation.setDuration(15000);
        rotationAnimation.setInterpolator(new LinearInterpolator());
        rotationAnimation.setRepeatMode(ValueAnimator.RESTART);
        rotationAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimation.start();
    }
}
