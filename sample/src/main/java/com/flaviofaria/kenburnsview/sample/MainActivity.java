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

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.flaviofaria.kenburnsview.KenBurnsView;


public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources r = getResources();

        Drawable d = r.getDrawable(R.drawable.img1);

        int w = FrameLayout.LayoutParams.MATCH_PARENT;
        int h = FrameLayout.LayoutParams.MATCH_PARENT;

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);

        KenBurnsView v = new KenBurnsView(this);
        v.setLayoutParams(lp);
        v.setImageDrawable(d);
        v.setScaleType(ImageView.ScaleType.CENTER_CROP);

        FrameLayout fl = new FrameLayout(this);
        fl.addView(v);
        setContentView(fl);
    }

}
