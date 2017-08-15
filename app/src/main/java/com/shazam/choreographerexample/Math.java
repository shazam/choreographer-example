/*
 * Copyright 2017 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.shazam.choreographerexample;

public abstract class Math {
    public static float mapFromPercent(float pc, float lBound, float uBound) {
        return map(pc, 0, 1, lBound, uBound);
    }

    public static float mapToPercent(float value, float lBound, float uBound) {
        if (uBound - lBound == 0) {
            return 0;
        }
        return map(value, lBound, uBound, 0, 1);
    }

    private static float map(float value,
                            float istart,
                            float istop,
                            float ostart,
                            float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }
}
