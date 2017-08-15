/*
 * Copyright 2017 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.shazam.choreographerexample.animation.interpolator;

import android.view.animation.Interpolator;

import static com.shazam.choreographerexample.Math.mapToPercent;

public class SequentialInterpolator implements Interpolator {
    private final float peakPoint;
    private final Interpolator prePeakInterpolator;
    private final Interpolator postPeakInterpolator;

    public SequentialInterpolator(float peakPoint, Interpolator prePeakInterpolator, Interpolator postPeakInterpolator) {
        this.peakPoint = peakPoint;
        this.prePeakInterpolator = prePeakInterpolator;
        this.postPeakInterpolator = postPeakInterpolator;
    }

    @Override
    public float getInterpolation(float input) {
        if (input <= peakPoint) {
            float translatedInput = mapToPercent(input, 0, peakPoint);
            return prePeakInterpolator.getInterpolation(translatedInput);
        } else {
            float translatedInput = mapToPercent(input, peakPoint, 1);
            return postPeakInterpolator.getInterpolation(translatedInput);
        }
    }
}