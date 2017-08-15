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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shazam.choreographerexample.animation.choreographer.FunkyView;

import static com.shazam.choreographerexample.FirstActivity.EXTRA_FROZEN_BUTTON;

public class SecondActivity extends AppCompatActivity {
    private FunkyView button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        button = findViewById(R.id.button);
        button.unfreezeState(getInitialState());
//        button.animateToState(FUNKY);   // Comment this out and try to spot a jump in the transition.
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private FunkyView.FrozenState getInitialState() {
        return getIntent().getParcelableExtra(EXTRA_FROZEN_BUTTON);
    }
}
