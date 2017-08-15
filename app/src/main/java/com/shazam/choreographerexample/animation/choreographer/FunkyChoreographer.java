/*
 * Copyright 2017 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.shazam.choreographerexample.animation.choreographer;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.shazam.choreographerexample.animation.LiteAnimator;
import com.shazam.choreographerexample.animation.interpolator.MirrorInterpolator;
import com.shazam.choreographerexample.animation.interpolator.SequentialInterpolator;

import static android.graphics.Color.HSVToColor;
import static android.graphics.Color.RED;
import static android.graphics.Color.colorToHSV;
import static com.shazam.choreographerexample.Math.mapFromPercent;
import static com.shazam.choreographerexample.animation.LiteAnimator.animator;

public class FunkyChoreographer implements Choreographer {
    private static final int SCALE_DURATION = 750;
    private static final int COLOR_DURATION = 4000;
    private static final int TY_DURATION = 3000;
    private static final float BTN_SCALE_MIN = 0.95f;
    private static final float BTN_SCALE_MAX = 1.05f;

    private final float[] hsvColor = new float[3];
    private final Interpolator internalScaleInterpolator = new AccelerateDecelerateInterpolator();
    private final Interpolator internalColorInterpolator = new LinearInterpolator();
    private final Interpolator internalTranslationInterpolator = new LinearInterpolator();
    private final LiteAnimator btnScaleAnimator = animator(SCALE_DURATION,
            new SequentialInterpolator(.5f, internalScaleInterpolator, new MirrorInterpolator(internalScaleInterpolator)));
    private final LiteAnimator colorAnimator = animator(COLOR_DURATION,
            new SequentialInterpolator(.5f, internalColorInterpolator, new MirrorInterpolator(internalColorInterpolator)));
    private final LiteAnimator translationYAnimator = animator(TY_DURATION,
            new SequentialInterpolator(.5f, internalTranslationInterpolator, new MirrorInterpolator(internalTranslationInterpolator)));

    private Frame lastFrame;

    @Override
    public Frame frameOn(long now) {
        if (lastFrame == null) {
            btnScaleAnimator.restart(now);
            colorAnimator.restart(now);
            translationYAnimator.restart(now);
            lastFrame = new Frame();
            lastFrame.scale = 1;
            lastFrame.alpha = 1f;
            lastFrame.color = RED;

            colorToHSV(lastFrame.color, hsvColor);
        }

        float scaleProgress = btnScaleAnimator.getAnimatedFraction(now);
        float colorAnimProgress = colorAnimator.getAnimatedFraction(now);
        float translationProgress = translationYAnimator.getAnimatedFraction(now);

        hsvColor[0] = mapFromPercent(colorAnimProgress, 0, 360);
        lastFrame.color = HSVToColor(hsvColor);
        lastFrame.scale = mapFromPercent(scaleProgress, BTN_SCALE_MIN, BTN_SCALE_MAX);
        lastFrame.translationY = mapFromPercent(translationProgress, -1, 1);

        return lastFrame;
    }

    @Override
    public long getStartTime() {
        return btnScaleAnimator.getStartTime();
    }
}
