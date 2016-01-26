package com.github.sundeepk.compactcalendarview;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;

class AnimationHandler {

    private CompactCalendarController compactCalendarController;
    private CompactCalendarView compactCalendarView;

    AnimationHandler(CompactCalendarController compactCalendarController, CompactCalendarView compactCalendarView) {
        this.compactCalendarController = compactCalendarController;
        this.compactCalendarView = compactCalendarView;
    }

    public final Property<CompactCalendarView, Float> INDICATOR_GROW_FACTOR = new Property<CompactCalendarView, Float>(Float.class, "growFactor") {
        @Override
        public void set(CompactCalendarView object, Float value) {
            compactCalendarView.invalidate();
            compactCalendarController.setGrowFactorIndicator(value);
        }

        @Override
        public Float get(CompactCalendarView object) {
            return compactCalendarController.getGrowFactorIndicator();
        }
    };

    void openCalendar(){
        Animation heightAnim = new CollapsingAnimation(compactCalendarView, compactCalendarController, compactCalendarView.getHeight(), true);

        final ObjectAnimator animIndicator = ObjectAnimator.ofFloat(compactCalendarView, INDICATOR_GROW_FACTOR, 1f, 55f);
        animIndicator.setDuration(700);
        animIndicator.setInterpolator(new OvershootInterpolator());
        animIndicator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                compactCalendarController.setAnimation(false);
                compactCalendarView.invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });


        compactCalendarController.setTargetHeight(compactCalendarView.getHeight());
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();

        compactCalendarController.setAnimationStarted(true);
        // compactCalendarController.setAnimation(true);

        heightAnim.setDuration(650);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        heightAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                compactCalendarController.setAnimationStarted(false);
                compactCalendarController.setAnimation(true);
                animIndicator.start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        compactCalendarView.startAnimation(heightAnim);
    }

}
