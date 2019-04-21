package com.github.sundeepk.compactcalendarview;


import android.animation.Animator;
import android.animation.ValueAnimator;
import androidx.annotation.NonNull;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;

class AnimationHandler {

    private static final int DEFAULT_HEIGHT_ANIM_DURATION_MILLIS = 650;
    private static final int DEFAULT_INDICATOR_ANIM_DURATION_MILLIS = 600;

    private int heightAnimDurationMillis = DEFAULT_HEIGHT_ANIM_DURATION_MILLIS;
    private int indicatorAnimDurationMillis = DEFAULT_INDICATOR_ANIM_DURATION_MILLIS;
    private boolean isAnimating = false;
    private CompactCalendarController compactCalendarController;
    private CompactCalendarView compactCalendarView;
    private CompactCalendarView.CompactCalendarAnimationListener compactCalendarAnimationListener;

    AnimationHandler(CompactCalendarController compactCalendarController, CompactCalendarView compactCalendarView) {
        this.compactCalendarController = compactCalendarController;
        this.compactCalendarView = compactCalendarView;
        if (compactCalendarController.getHeightAnimDurationMillis() > -1) {
            heightAnimDurationMillis = compactCalendarController.getHeightAnimDurationMillis();
        }
        if (compactCalendarController.getIndicatorAnimDurationMillis() > -1) {
            indicatorAnimDurationMillis = compactCalendarController.getIndicatorAnimDurationMillis();
        }
    }

    void setCompactCalendarAnimationListener(CompactCalendarView.CompactCalendarAnimationListener compactCalendarAnimationListener){
        this.compactCalendarAnimationListener = compactCalendarAnimationListener;
    }

    void setHeightAnimDuration(int durationMillis) {
        this.heightAnimDurationMillis = durationMillis;
    }

    void setIndicatorAnimDuration(int durationMillis) {
        this.indicatorAnimDurationMillis = durationMillis;
    }

    void openCalendar() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        Animation heightAnim = getCollapsingAnimation(true);
        heightAnim.setDuration(heightAnimDurationMillis);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPAND_COLLAPSE_CALENDAR);
        setUpAnimationLisForOpen(heightAnim);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();
        compactCalendarView.startAnimation(heightAnim);
    }

    void closeCalendar() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        Animation heightAnim = getCollapsingAnimation(false);
        heightAnim.setDuration(heightAnimDurationMillis);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        setUpAnimationLisForClose(heightAnim);
        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPAND_COLLAPSE_CALENDAR);
        compactCalendarView.getLayoutParams().height = compactCalendarView.getHeight();
        compactCalendarView.requestLayout();
        compactCalendarView.startAnimation(heightAnim);
    }

    void openCalendarWithAnimation() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        final Animator indicatorAnim = getIndicatorAnimator(1f, compactCalendarController.getDayIndicatorRadius());
        final Animation heightAnim = getExposeCollapsingAnimation(true);
        compactCalendarView.getLayoutParams().height = 0;
        compactCalendarView.requestLayout();
        setUpAnimationLisForExposeOpen(indicatorAnim, heightAnim);
        compactCalendarView.startAnimation(heightAnim);
    }

    void closeCalendarWithAnimation() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        final Animator indicatorAnim = getIndicatorAnimator(compactCalendarController.getDayIndicatorRadius(), 1f);
        final Animation heightAnim = getExposeCollapsingAnimation(false);
        compactCalendarView.getLayoutParams().height = compactCalendarView.getHeight();
        compactCalendarView.requestLayout();
        setUpAnimationLisForExposeClose(indicatorAnim, heightAnim);
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
                onOpen();
                isAnimating = false;
            }
        });
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
                onClose();
                isAnimating = false;
            }
        });
        indicatorAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.ANIMATE_INDICATORS);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
    }

    @NonNull
    private Animation getExposeCollapsingAnimation(final boolean isCollapsing) {
        Animation heightAnim = getCollapsingAnimation(isCollapsing);
        heightAnim.setDuration(heightAnimDurationMillis);
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
        animIndicator.setDuration(indicatorAnimDurationMillis);
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

    private void onOpen() {
        if (compactCalendarAnimationListener != null) {
            compactCalendarAnimationListener.onOpened();
        }
    }

    private void onClose() {
        if (compactCalendarAnimationListener != null) {
            compactCalendarAnimationListener.onClosed();
        }
    }

    private void setUpAnimationLisForOpen(Animation openAnimation) {
        openAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                onOpen();
                isAnimating = false;
            }
        });
    }

    private void setUpAnimationLisForClose(Animation openAnimation) {
        openAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                onClose();
                isAnimating = false;
            }
        });
    }

    public boolean isAnimating() {
        return isAnimating;
    }
}
