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

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public abstract class KenBurnsActivity extends AppCompatActivity {

    private boolean mPaused;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.playPause);
        if (mPaused) {
            item.setIcon(R.drawable.ic_play);
            item.setTitle(R.string.play);
        } else {
            item.setIcon(R.drawable.ic_pause);
            item.setTitle(R.string.pause);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.playPause:
                if (mPaused) {
                    onPlayClick();
                } else {
                    onPauseClick();
                }
                mPaused = !mPaused;
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    protected abstract void onPlayClick();
    protected abstract void onPauseClick();

}
