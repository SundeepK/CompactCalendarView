package com.github.sundeepk.compactcalendarview;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
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

    void openCalendar(){
        final Animator indicatorAnim = getIndicatorAnimator(1f, 55f);
        final Animation heightAnim = getCollapsingAnimation(indicatorAnim, true);

        compactCalendarController.setTargetHeight(compactCalendarView.getHeight());
        compactCalendarController.setAnimatingHeight(true);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    void closeCalendar(){
        final Animator indicatorAnim = getIndicatorAnimator(55f, 1f);
        final Animation heightAnim = getCollapsingAnimation(indicatorAnim, false);

        compactCalendarController.setTargetHeight(compactCalendarView.getHeight());
        compactCalendarController.setAnimatingHeight(true);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    @NonNull
    private Animation getCollapsingAnimation(final Animator animIndicator, final boolean isCollapsing) {
        Animation heightAnim = new CollapsingAnimation(compactCalendarView, compactCalendarController, compactCalendarView.getHeight(), isCollapsing);
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
    private Animator getIndicatorAnimator(float from, float to) {
        ValueAnimator animIndicator = ValueAnimator.ofFloat(from, to);
        animIndicator.setDuration(INDICATOR_ANIM_DURATION_MILLIS);
        animIndicator.setInterpolator(new OvershootInterpolator());
        animIndicator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                compactCalendarView.invalidate();
                compactCalendarController.setGrowFactorIndicator((Float) animation.getAnimatedValue());
            }
        });
        animIndicator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                compactCalendarController.setAnimation(false);
                compactCalendarView.invalidate();
            }
        });
        return animIndicator;
    }

}
