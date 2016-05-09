package com.github.sundeepk.compactcalendarview;


import android.view.animation.Animation;
import android.view.animation.Transformation;

class CollapsingAnimation extends Animation {
    private final int targetHeight;
    private final CompactCalendarView view;
    private int targetGrowRadius;
    private final boolean down;
    private CompactCalendarController compactCalendarController;

    public CollapsingAnimation(CompactCalendarView view, CompactCalendarController compactCalendarController, int targetHeight, int targetGrowRadius, boolean down) {
        this.view = view;
        this.compactCalendarController = compactCalendarController;
        this.targetHeight = targetHeight;
        this.targetGrowRadius = targetGrowRadius;
        this.down = down;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float grow = 0;
        int newHeight;
        if (down) {
            newHeight = (int) (targetHeight * interpolatedTime);
            grow = (interpolatedTime * (targetGrowRadius * 2));
        } else {
            float progress = 1 - interpolatedTime;
            newHeight = (int) (targetHeight * progress);
            grow = (progress * (targetGrowRadius * 2));
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