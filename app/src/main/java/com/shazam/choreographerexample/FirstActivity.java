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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.shazam.choreographerexample.animation.choreographer.FunkyView;

import static com.shazam.choreographerexample.animation.choreographer.FunkyView.State.DULL;
import static com.shazam.choreographerexample.animation.choreographer.FunkyView.State.FUNKY;
import static com.shazam.choreographerexample.animation.choreographer.TransitioningChoreographer.TRANSITION_DURATION;

/**
 * Pressing the button, immediately makes it animate to the FUNKY state. Halfway through that
 * transition, we actually launch the new activity to showcase how the state transfer doesn't
 * care if we're mid-animation/transition and just continues the same animations.
 */
public class FirstActivity extends AppCompatActivity {
    static final String EXTRA_FROZEN_BUTTON = "com.shazam.choreographer.extra.FROZEN_BUTTON";

    private FunkyView button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        button = findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                button.animateToState(FUNKY);

                button.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                        intent.putExtra(EXTRA_FROZEN_BUTTON, button.freezeState());
                        startActivityForResult(intent, 0);
                    }
                }, TRANSITION_DURATION / 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            button.animateToState(DULL);
        }
    }
}