package com.github.sundeepk.compactcalendarview;


import android.view.animation.Animation;
import android.view.animation.Transformation;

class CollapsingAnimation extends Animation {
    private final int targetHeight;
    private final CompactCalendarView view;
    private final boolean down;
    private float currentGrow;
    private CompactCalendarController compactCalendarController;

    public CollapsingAnimation(CompactCalendarView view, CompactCalendarController compactCalendarController, int targetHeight, boolean down) {
        this.view = view;
        this.compactCalendarController = compactCalendarController;
        this.targetHeight = targetHeight;
        this.down = down;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        currentGrow+=2.4;
        float grow = 0;
        int newHeight;
        if (down) {
            newHeight = (int) (targetHeight * interpolatedTime);
            grow = (float) (interpolatedTime * (targetHeight * 2));
        } else {
            float progress = 1 - interpolatedTime;
            newHeight = (int) (targetHeight * progress);
            grow = (float) (progress * (targetHeight * 2));
        }
        compactCalendarController.setGrowProgress(grow);
        view.getLayoutParams().height = newHeight;
        view.requestLayout();

    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}