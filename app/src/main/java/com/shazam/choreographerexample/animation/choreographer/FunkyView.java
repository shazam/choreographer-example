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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import static android.os.SystemClock.uptimeMillis;
import static com.shazam.choreographerexample.animation.choreographer.FunkyView.State.DULL;
import static java.lang.Math.max;

public class FunkyView extends View {
    public enum State {
        DULL,
        FUNKY
    }

    private final TransitioningChoreographer choreographer = new TransitioningChoreographer(DULL);
    private final Paint buttonPaint = new Paint();
    private int buttonMaxWidth = 0;
    private float maxTranslationY = 0;

    public FunkyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        buttonPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxPadding = max(max(max(getPaddingBottom(), getPaddingTop()), getPaddingLeft()), getPaddingRight());
        buttonMaxWidth = getMeasuredWidth() - maxPadding;
        maxTranslationY = getMeasuredWidth() * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Frame f = choreographer.frameOn(uptimeMillis());
        float cx = getWidth() / 2;
        float cy = getHeight() / 2;
        int radius = (int) (buttonMaxWidth * f.scale) / 2;

        setTranslationY(f.translationY * maxTranslationY);
        buttonPaint.setColor(f.color);
        buttonPaint.setAlpha((int) (f.alpha * 255));
        canvas.drawCircle(cx, cy, radius, buttonPaint);

        invalidate();
    }

    public void animateToState(State state) {
        choreographer.animateToState(state);
    }

    public FrozenState freezeState() {
        return new FrozenState(choreographer.getState());
    }

    public void unfreezeState(FrozenState icicle) {
        choreographer.restoreFromState(icicle.choreographerState);
    }

    /**
     * Call to immediately start a transition back to the state passed in the last
     * {@link #unfreezeState(FrozenState)} call. No effect if never called.
     */
    public void transitionToFrozen(FrozenState icicle, long duration) {
        // Create a franken-state, transitioning from current, to frozen's latest, which is what was running at freezing time.
        // This should ensure that the one we're transitioning to will have already been started at the correct time.
        // This is in contrast to calling animateToState(latestFrozenState) which would restart the internal choreographer
        // and we wouldn't be able to match the frozen button's animation timing.
        TransitioningChoreographer.State choreographerState = icicle.choreographerState;
        Choreographer targetChoreographer = TransitioningChoreographer.resurrectChoreographer(choreographerState.taggingStates[0], choreographerState.startTimes[0]);
        choreographer.animateToChoreographer(targetChoreographer, duration);
    }

    public static class FrozenState implements Parcelable {
        private final TransitioningChoreographer.State choreographerState;

        /**
         * @param state Internal choreographer state. Required so that the button continues the exact same movement when unfrozen.
         */
        private FrozenState(TransitioningChoreographer.State state) {
            choreographerState = state;
        }

        private FrozenState(Parcel in) {
            choreographerState = in.readParcelable(TransitioningChoreographer.State.class.getClassLoader());
        }

        public static final Creator<FrozenState> CREATOR = new Creator<FrozenState>() {
            @Override
            public FrozenState createFromParcel(Parcel in) {
                return new FrozenState(in);
            }

            @Override
            public FrozenState[] newArray(int size) {
                return new FrozenState[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(choreographerState, flags);
        }
    }
}
