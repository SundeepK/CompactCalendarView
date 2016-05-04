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
    private int targetGrowRadius;

    AnimationHandler(CompactCalendarController compactCalendarController, CompactCalendarView compactCalendarView) {
        this.compactCalendarController = compactCalendarController;
        this.compactCalendarView = compactCalendarView;
    }

    void openCalendar(){
        Animation heightAnim = getCollapsingAnimation(true);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPAND_COLLAPSE_CALENDAR);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    void closeCalendar(){
        Animation heightAnim = getCollapsingAnimation(false);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPAND_COLLAPSE_CALENDAR);
        compactCalendarView.getLayoutParams().height = compactCalendarView.getHeight();
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    void openCalendarWithAnimation(){
        final Animator indicatorAnim = getIndicatorAnimator(1f, compactCalendarController.getDayIndicatorRadius());
        final Animation heightAnim = getCollapsingAnimation(indicatorAnim, true);

        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPOSE_CALENDAR_ANIMATION);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();

        compactCalendarView.startAnimation(heightAnim);
    }

    void closeCalendarWithAnimation(){
        final Animator indicatorAnim = getIndicatorAnimator(compactCalendarController.getDayIndicatorRadius(), 1f);
        final Animation heightAnim = getCollapsingAnimation(indicatorAnim, false);

        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPOSE_CALENDAR_ANIMATION);
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
                    compactCalendarController.setAnimationStatus(CompactCalendarController.ANIMATE_INDICATORS);
                    compactCalendarController.setAnimatingIndicators(true);
                    animIndicator.start();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isCollapsing){
                    compactCalendarController.setAnimatingIndicators(true);
                    animIndicator.start();
                } else {
                    compactCalendarController.setAnimatingIndicators(false);
                }
                compactCalendarController.setAnimationStatus(CompactCalendarController.IDLE);
            }
        });
        return heightAnim;
    }

    @NonNull
    private Animation getCollapsingAnimation(boolean isCollapsing) {
        return new CollapsingAnimation(compactCalendarView, compactCalendarController, compactCalendarController.getTargetHeight(), getTargetGrowRadius(), isCollapsing);
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

    private int getTargetGrowRadius() {
        int heightSq = compactCalendarController.getTargetHeight() * compactCalendarController.getTargetHeight();
        int widthSq = compactCalendarController.getWidth() * compactCalendarController.getWidth();
        return (int) (0.5 * Math.sqrt(heightSq + widthSq));
    }
}
