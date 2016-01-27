package com.github.sundeepk.compactcalendarview;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;

class AnimationHandler {

    public static final int HEIGHT_ANIM_DURATION_MILLIS = 650;
    public static final int INDICATOR_ANIM_DURATION_MILLIS = 700;
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
        final ObjectAnimator indicatorAnim = getIndicatorAnimator();
        final Animation heightAnim = getCollapsingAnimation(indicatorAnim);

        compactCalendarController.setTargetHeight(compactCalendarView.getHeight());
        compactCalendarController.setAnimatingHeight(true);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    @NonNull
    private Animation getCollapsingAnimation(final ObjectAnimator animIndicator) {
        Animation heightAnim = new CollapsingAnimation(compactCalendarView, compactCalendarController, compactCalendarView.getHeight(), true);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        heightAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                compactCalendarController.setAnimatingHeight(false);
                compactCalendarController.setAnimation(true);
                animIndicator.start();

            }
        });
        return heightAnim;
    }

    @NonNull
    private ObjectAnimator getIndicatorAnimator() {
        final ObjectAnimator animIndicator = ObjectAnimator.ofFloat(compactCalendarView, INDICATOR_GROW_FACTOR, 1f, 55f);
        animIndicator.setDuration(INDICATOR_ANIM_DURATION_MILLIS);
        animIndicator.setInterpolator(new OvershootInterpolator());
        animIndicator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                compactCalendarController.setAnimation(false);
                compactCalendarView.invalidate();
            }
        });
        return animIndicator;
    }

}
