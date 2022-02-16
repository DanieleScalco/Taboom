package it.bastoner.taboom.animations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

public class Animations {

    // Support class to get animations
    // Animators can modify the positions of Views for real, Animations not
    // (you see animations but View don't move for real)

    public static void doReduceIncreaseAnimation(View view, Animator.AnimatorListener listener) {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, Button.SCALE_Y, 0.8f);
        animatorX.setDuration(150);
        animatorX.setRepeatMode(ValueAnimator.REVERSE);
        animatorX.setRepeatCount(1);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, Button.SCALE_X, 0.8f);
        animatorY.setDuration(150);
        animatorY.setRepeatMode(ValueAnimator.REVERSE);
        animatorY.setRepeatCount(1);

        AnimatorSet animatorSet = new AnimatorSet();
        if (listener != null)
            animatorSet.addListener(listener);
        animatorSet.play(animatorX).with(animatorY);
        animatorSet.start();
    }

    public static void doReduceAnimation(View view, Animator.AnimatorListener listener) {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, Button.SCALE_Y, 0.8f);
        animatorX.setDuration(150);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, Button.SCALE_X, 0.8f);
        animatorY.setDuration(150);

        AnimatorSet animatorSet = new AnimatorSet();
        if (listener != null)
            animatorSet.addListener(listener);
        animatorSet.play(animatorX).with(animatorY);
        animatorSet.start();
    }

    public static void doIncreaseAnimation(View view, Animator.AnimatorListener listener) {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, Button.SCALE_Y, 1f);
        animatorX.setDuration(150);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, Button.SCALE_X, 1f);
        animatorY.setDuration(150);

        AnimatorSet animatorSet = new AnimatorSet();
        if (listener != null)
            animatorSet.addListener(listener);
        animatorSet.play(animatorX).with(animatorY);
        animatorSet.start();
    }

    public static void doSpinReduceIncreaseAnimation(View view, Animator.AnimatorListener listener) {

        ObjectAnimator animatorSpin = ObjectAnimator.ofFloat(view, Button.ROTATION, 0, 360);
        animatorSpin.setDuration(350);
        animatorSpin.setInterpolator(new LinearInterpolator());
        animatorSpin.setRepeatCount(1);

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, Button.SCALE_Y, 0.75f);
        animatorX.setDuration(350);
        animatorX.setRepeatMode(ValueAnimator.REVERSE);
        animatorX.setRepeatCount(1);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, Button.SCALE_X, 0.75f);
        animatorY.setDuration(350);
        animatorY.setRepeatMode(ValueAnimator.REVERSE);
        animatorY.setRepeatCount(1);

        AnimatorSet animatorSet = new AnimatorSet();
        if (listener != null)
            animatorSet.addListener(listener);
        animatorSet.play(animatorSpin).with(animatorX).with(animatorY);
        animatorSet.start();

    }

}
