/*
 * Copyright 2014 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flaviofaria.kenburnsview.sample;

import android.os.Bundle;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;

import static com.flaviofaria.kenburnsview.KenBurnsView.TransitionListener;


public class MainActivity extends SherlockActivity implements TransitionListener {

    private static final int TRANSITIONS_TO_SWITCH = 3;

    private ViewSwitcher mViewSwitcher;

    private boolean mPaused;
    private int mTransitionsCount = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        KenBurnsView img1 = (KenBurnsView) findViewById(R.id.img1);
        img1.setTransitionListener(this);

        KenBurnsView img2 = (KenBurnsView) findViewById(R.id.img2);
        img2.setTransitionListener(this);
    }


    @Override
    public void onTransitionStart(Transition transition) {

    }


    @Override
    public void onTransitionEnd(Transition transition) {
        mTransitionsCount++;
        if (mTransitionsCount == TRANSITIONS_TO_SWITCH) {
            mViewSwitcher.showNext();
            mTransitionsCount = 0;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.playPause);
        if (mPaused) {
            item.setIcon(R.drawable.ic_media_play);
            item.setTitle(R.string.play);
        } else {
            item.setIcon(R.drawable.ic_media_pause);
            item.setTitle(R.string.pause);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.playPause:
                KenBurnsView currentImage = (KenBurnsView) mViewSwitcher.getCurrentView();
                if (mPaused) {
                    currentImage.resume();
                } else {
                    currentImage.pause();
                }
                mPaused = !mPaused;
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
