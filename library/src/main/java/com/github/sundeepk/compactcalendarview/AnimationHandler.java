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
        Animation heightAnim = getCollapsingAnimation(true);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        compactCalendarController.setAnimatingHeight(false);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    void closeCalendar(){
        Animation heightAnim = getCollapsingAnimation(false);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        compactCalendarController.setAnimatingHeight(false);
        compactCalendarView.getLayoutParams().height = compactCalendarView.getHeight();
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    void openCalendarWithAnimation(){
        final Animator indicatorAnim = getIndicatorAnimator(1f, 55f);
        final Animation heightAnim = getCollapsingAnimation(indicatorAnim, true);

        compactCalendarController.setAnimatingHeight(true);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    void closeCalendarWithAnimation(){
        final Animator indicatorAnim = getIndicatorAnimator(55f, 1f);
        final Animation heightAnim = getCollapsingAnimation(indicatorAnim, false);

        compactCalendarController.setAnimatingHeight(true);
        compactCalendarView.getLayoutParams().height = compactCalendarView.getHeight();
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    @NonNull
    private Animation getCollapsingAnimation(final Animator animIndicator, final boolean isCollapsing) {
        Animation heightAnim = getCollapsingAnimation(isCollapsing);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        heightAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if(!isCollapsing){
                    compactCalendarController.setAnimatingHeight(false);
                    compactCalendarController.setAnimatingIndicators(true);
                    animIndicator.start();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isCollapsing){
                    compactCalendarController.setAnimatingHeight(false);
                    compactCalendarController.setAnimatingIndicators(true);
                    animIndicator.start();
                } else {
                    compactCalendarController.setAnimatingIndicators(false);
                }
            }
        });
        return heightAnim;
    }

    @NonNull
    private Animation getCollapsingAnimation(boolean isCollapsing) {
        return new CollapsingAnimation(compactCalendarView, compactCalendarController, compactCalendarController.getTargetHeight(), isCollapsing);
    }

    @NonNull
    private Animator getIndicatorAnimator(float from, float to) {
        ValueAnimator animIndicator = ValueAnimator.ofFloat(from, to);
        animIndicator.setDuration(INDICATOR_ANIM_DURATION_MILLIS);
        animIndicator.setInterpolator(new OvershootInterpolator());
        animIndicator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                compactCalendarController.setGrowFactorIndicator((Float) animation.getAnimatedValue());
                compactCalendarView.invalidate();
            }
        });
        animIndicator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                compactCalendarController.setAnimatingIndicators(false);
                compactCalendarView.invalidate();
            }
        });
        return animIndicator;
    }

}
