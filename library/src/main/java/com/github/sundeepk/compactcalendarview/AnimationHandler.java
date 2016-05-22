package com.github.sundeepk.compactcalendarview;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;

class AnimationHandler {

    public static final int HEIGHT_ANIM_DURATION_MILLIS = 650;
    public static final int INDICATOR_ANIM_DURATION_MILLIS = 600;
    private CompactCalendarController compactCalendarController;
    private CompactCalendarView compactCalendarView;

    AnimationHandler(CompactCalendarController compactCalendarController, CompactCalendarView compactCalendarView) {
        this.compactCalendarController = compactCalendarController;
        this.compactCalendarView = compactCalendarView;
    }

    void openCalendar() {
        Animation heightAnim = getCollapsingAnimation(true);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPAND_COLLAPSE_CALENDAR);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();
        compactCalendarView.startAnimation(heightAnim);
    }

    void closeCalendar() {
        Animation heightAnim = getCollapsingAnimation(false);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPAND_COLLAPSE_CALENDAR);
        compactCalendarView.getLayoutParams().height = compactCalendarView.getHeight();
        compactCalendarView.requestLayout();
        compactCalendarView.startAnimation(heightAnim);
    }

    void openCalendarWithAnimation() {
        final Animator indicatorAnim = getIndicatorAnimator(1f, compactCalendarController.getDayIndicatorRadius());
        final Animation heightAnim = getExposeCollapsingAnimation(true);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();
        setUpAnimationLisForExposeOpen(indicatorAnim, heightAnim);
        compactCalendarView.startAnimation(heightAnim);
    }

    private void setUpAnimationLisForExposeOpen(final Animator indicatorAnim, Animation heightAnim) {
        heightAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.EXPOSE_CALENDAR_ANIMATION);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                indicatorAnim.start();
            }
        });
        indicatorAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.ANIMATE_INDICATORS);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.IDLE);
            }
        });
    }

    void closeCalendarWithAnimation() {
        final Animator indicatorAnim = getIndicatorAnimator(compactCalendarController.getDayIndicatorRadius(), 1f);
        final Animation heightAnim = getExposeCollapsingAnimation(false);
        compactCalendarView.getLayoutParams().height = compactCalendarView.getHeight();
        compactCalendarView.requestLayout();
        setUpAnimationLisForExposeClose(indicatorAnim, heightAnim);
        compactCalendarView.startAnimation(heightAnim);
    }

    private void setUpAnimationLisForExposeClose(final Animator indicatorAnim, Animation heightAnim) {
        heightAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.EXPOSE_CALENDAR_ANIMATION);
                indicatorAnim.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.IDLE);
            }
        });
        indicatorAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.ANIMATE_INDICATORS);
            }
        });
    }

    @NonNull
    private Animation getExposeCollapsingAnimation(final boolean isCollapsing) {
        Animation heightAnim = getCollapsingAnimation(isCollapsing);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
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
        return animIndicator;
    }

    private int getTargetGrowRadius() {
        int heightSq = compactCalendarController.getTargetHeight() * compactCalendarController.getTargetHeight();
        int widthSq = compactCalendarController.getWidth() * compactCalendarController.getWidth();
        return (int) (0.5 * Math.sqrt(heightSq + widthSq));
    }
}
