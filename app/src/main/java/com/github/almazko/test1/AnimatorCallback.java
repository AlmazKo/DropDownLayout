package com.github.almazko.test1;

import android.animation.ValueAnimator;
import android.util.Log;

/**
 * @author Alexander Suslov
 */
public abstract class AnimatorCallback {

    public static interface Callback {
        public void call();
    }

    public static ValueAnimator get(final Callback callback) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 0);
        animator.setDuration(0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean isCall = false;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (!isCall) {
                    isCall = true;
                    callback.call();
                    Log.v("AnimatorCallback", "call!");
                }
            }
        });


        return animator;
    }
}
