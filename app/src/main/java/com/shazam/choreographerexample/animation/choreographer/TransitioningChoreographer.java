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

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

import com.shazam.choreographerexample.animation.LiteAnimator;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.os.SystemClock.uptimeMillis;
import static com.shazam.choreographerexample.Math.mapFromPercent;
import static com.shazam.choreographerexample.animation.choreographer.FunkyView.State.DULL;
import static com.shazam.choreographerexample.animation.choreographer.FunkyView.State.FUNKY;
import static java.lang.Math.min;

public class TransitioningChoreographer implements Choreographer {
    public static final long TRANSITION_DURATION = 1000;

    private final Deque<Choreographer> choreographers = new ArrayDeque<>(2);
    private final LiteAnimator transitionAnimator;
    private final Frame frame = new Frame();

    TransitioningChoreographer(FunkyView.State state) {
        transitionAnimator = LiteAnimator.animator(0, new FastOutSlowInInterpolator());
        transitionAnimator.setNoRepeat(true);

        Choreographer c = choreographerFromButtonState(state);
        choreographers.addFirst(c);
        choreographers.addLast(c);
    }

    void animateToState(FunkyView.State state) {
        if (state == buttonStateFrom(choreographers.getFirst())) {
            return;
        }

        animateToChoreographer(choreographerFromButtonState(state), TRANSITION_DURATION);
    }

    void animateToChoreographer(Choreographer choreographer, long duration) {
        if (choreographers.size() == 2) {
            choreographers.removeLast();
        }
        choreographers.offerFirst(choreographer);
        transitionAnimator.setDuration(duration);
        transitionAnimator.restart(uptimeMillis());
    }

    void restoreFromState(State choreographerState) {
        choreographers.clear();
        for (int i = 0; i < min(choreographerState.startTimes.length, 2); i++) {
            choreographers.addLast(
                    resurrectChoreographer(
                            choreographerState.taggingStates[i], choreographerState.startTimes[i]));
        }
        transitionAnimator.restart(choreographerState.transitionStartTime);
    }

    @Override
    public Frame frameOn(long now) {
        float progress = transitionAnimator.getAnimatedFraction(now);
        Frame from = choreographers.getLast().frameOn(now);
        Frame to = choreographers.getFirst().frameOn(now);

        int red = (int) (mapFromPercent(progress, red(from.color), red(to.color)));
        int green = (int) (mapFromPercent(progress, green(from.color), green(to.color)));
        int blue = (int) (mapFromPercent(progress, blue(from.color), blue(to.color)));
        frame.color = Color.argb(255, red, green, blue);
        frame.scale = mapFromPercent(progress, from.scale, to.scale);
        frame.alpha = mapFromPercent(progress, from.alpha, to.alpha);
        frame.translationY = mapFromPercent(progress, from.translationY, to.translationY);

        return frame;
    }

    @Override
    public long getStartTime() {
        return min(choreographers.getFirst().getStartTime(), choreographers.getLast().getStartTime());
    }

    State getState() {
        return new State(choreographers, transitionAnimator.getStartTime());
    }

    private static Choreographer choreographerFromButtonState(FunkyView.State state) {
        switch (state) {
            case DULL:
                return new DullChoreographer();
            default:
            case FUNKY:
                return new FunkyChoreographer();
        }
    }

    private static FunkyView.State buttonStateFrom(Choreographer c) {
        if (c instanceof DullChoreographer) {
            return DULL;
        } else {
            return FUNKY;
        }
    }

    static Choreographer resurrectChoreographer(FunkyView.State state, long startTime) {
        Choreographer c = choreographerFromButtonState(state);
        c.frameOn(startTime);    // aka restart
        return c;
    }

    public static class State implements Parcelable {
        FunkyView.State[] taggingStates = new FunkyView.State[2];
        long[] startTimes = new long[2];
        long transitionStartTime;

        State(Collection<Choreographer> choreographers, long transitionStartTime) {
            this.transitionStartTime = transitionStartTime;
            int i = 0;
            for (Choreographer choreographer : choreographers) {
                startTimes[i] = choreographer.getStartTime();
                taggingStates[i] = buttonStateFrom(choreographer);
                i++;
            }
        }

        private State(Parcel in) {
            int[] ordinals = new int[2];
            in.readIntArray(ordinals);
            in.readLongArray(startTimes);
            this.transitionStartTime = in.readLong();

            int i = 0;
            for (int ordinal : ordinals) {
                taggingStates[i++] = FunkyView.State.values()[ordinal];
            }
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            int[] statesArray = new int[]{taggingStates[0].ordinal(), taggingStates[1].ordinal()};
            dest.writeIntArray(statesArray);
            dest.writeLongArray(startTimes);
            dest.writeLong(transitionStartTime);
        }
    }
}
