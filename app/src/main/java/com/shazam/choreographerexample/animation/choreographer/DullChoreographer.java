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

import com.shazam.choreographerexample.animation.LiteAnimator;
import com.shazam.choreographerexample.animation.interpolator.MirrorInterpolator;
import com.shazam.choreographerexample.animation.interpolator.SequentialInterpolator;

import static android.graphics.Color.CYAN;
import static com.shazam.choreographerexample.Math.mapFromPercent;
import static com.shazam.choreographerexample.animation.LiteAnimator.animator;

public class DullChoreographer implements Choreographer {
    private static final int DURATION = 1500;
    private static final float BTN_SCALE_MIN = 0.90f;
    private static final float BTN_SCALE_MAX = 0.95f;

    private final Interpolator internalInterpolator = new AccelerateDecelerateInterpolator();
    private final LiteAnimator btnScaleAnimator = animator(DURATION,
            new SequentialInterpolator(.5f, internalInterpolator, new MirrorInterpolator(internalInterpolator)));

    private Frame lastFrame;

    @Override
    public Frame frameOn(long now) {
        if (lastFrame == null) {
            btnScaleAnimator.restart(now);
            lastFrame = new Frame();
            lastFrame.scale = 1;
            lastFrame.alpha = .2f;
            lastFrame.translationY = 0f;
            lastFrame.color = CYAN;
        }

        float animatedFraction = btnScaleAnimator.getAnimatedFraction(now);
        lastFrame.scale = mapFromPercent(animatedFraction, BTN_SCALE_MIN, BTN_SCALE_MAX);

        return lastFrame;
    }

    @Override
    public long getStartTime() {
        return btnScaleAnimator.getStartTime();
    }
}
