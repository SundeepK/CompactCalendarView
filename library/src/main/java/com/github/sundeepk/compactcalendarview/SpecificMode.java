package com.github.sundeepk.compactcalendarview;

import android.support.annotation.IntDef;

@IntDef({
        SpecificMode.NONE,
        SpecificMode.ENABLED_DATES,
        SpecificMode.DISABLED_DATES,
})
public @interface SpecificMode {
    int NONE = 0;
    int ENABLED_DATES = 1;
    int DISABLED_DATES = 2;
}
