/*
 * Copyright 2017 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.shazam.choreographerexample.animation;

import android.view.animation.Interpolator;

import static java.lang.Math.min;

/**
 * Infinitely repeating animator that moves from 0 to 1 in durationMs.
 */
public class LiteAnimator {
    private long startTime = 0;
    private long durationMs;
    private Interpolator interpolator;
    private boolean noRepeat;

    private LiteAnimator() {
    }

    /**
     * @param durationMs   How long it should take for the animation to run a full circle.
     * @param interpolator How to interpolate values returned by getValue().
     * @return An animator to use.
     */
    public static LiteAnimator animator(long durationMs, Interpolator interpolator) {
        LiteAnimator animator = new LiteAnimator();
        animator.setDuration(durationMs);
        animator.interpolator = interpolator;

        return animator;
    }

    public void setDuration(long durationMs) {
        if (durationMs == 0) {
            durationMs = 1;
        }

        this.durationMs = durationMs;
    }

    public void setNoRepeat(boolean noRepeat) {
        this.noRepeat = noRepeat;
    }

    public void restart(long now) {
        startTime = now;
    }

    public float getAnimatedFraction(long now) {
        return getAnimatedFraction(now, 0);
    }

    /**
     * @param startDelay Same effect as delaying start, but doesn't actually wait.
     */
    public float getAnimatedFraction(long now, long startDelay) {
        return getAnimatedFraction(now, startDelay, 0);
    }

    public float getAnimatedFraction(long now, long startDelay, long endDelay) {
        return getAnimatedFraction(now, startDelay, endDelay, 0, durationMs);
    }

    public float getAnimatedFraction(long now, long startDelay, long endDelay, long startCrop, long croppedDuration) {
        return getAnimatedFraction(now, startDelay, endDelay, startCrop, croppedDuration, 0);
    }

    public float getAnimatedFraction(long now, long startDelay, long endDelay, long startCrop, long croppedDuration, long stagger) {
        return getAnimatedFractionInternal(now, durationMs, startDelay, endDelay, startCrop, croppedDuration, stagger);
    }

    /**
     * Same as {@link #getAnimatedFraction(long, long)} but overrides the duration passed in the constructor.
     */
    private float getAnimatedFractionInternal(long now, long fullDuration, long startDelay, long endDelay, long startCrop, long croppedDuration, long stagger) {
        long elapsed = now - startTime;
        if (noRepeat) {
            elapsed = min(elapsed, croppedDuration - startCrop);
        } else {
            elapsed = elapsed % croppedDuration;
        }
        elapsed += startCrop;

        return getAnimatedDelayedFraction(elapsed - stagger, fullDuration, startDelay, endDelay);
    }

    private float getAnimatedDelayedFraction(float elapsed,
                                             float fullDuration,
                                             float startDelay, float endDelay) {
        float effectiveDuration = fullDuration - startDelay - endDelay;

        if (elapsed < startDelay) {
            return interpolator.getInterpolation(0);
        } else if (elapsed > fullDuration - endDelay) {
            return interpolator.getInterpolation(1);
        } else {
            return interpolator.getInterpolation((elapsed - startDelay) / effectiveDuration);
        }
    }

    public long getStartTime() {
        return startTime;
    }
}
