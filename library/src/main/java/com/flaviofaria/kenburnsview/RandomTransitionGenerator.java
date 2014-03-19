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
package com.flaviofaria.kenburnsview;

import android.graphics.RectF;

import java.util.Random;

public class RandomTransitionGenerator implements TransitionGenerator {

    /** Minimum rect dimension factor, according to the maximum one. */
    private static final float MIN_RECT_FACTOR = 0.5f;

    /** Random object used to generate arbitrary rects. */
    private final Random mRandom = new Random(System.currentTimeMillis());

    /** The last generated transition. */
    private Transition mLastGenTrans;


    @Override
    public Transition generateNextTransition(RectF viewport, RectF drawableBounds) {
        RectF srcRect;
        if (mLastGenTrans == null) {
            srcRect = generateRandomRect(viewport, drawableBounds);
        } else {
            srcRect = mLastGenTrans.getDestinyRect();
        }
        mLastGenTrans = new Transition(srcRect, generateRandomRect(viewport, drawableBounds));
        return mLastGenTrans;
    }


    /**
     * Generates a random rect in as scale that varies between
     * {@link #MIN_RECT_FACTOR} and 1. This scale is relative to {@code viewPortRect} and
     * will be 1 when {@code viewPortRect} and the generated rect are exactly the same dimensions.
     * @return an arbitrary generated rect smaller or equals than {@link #mViewPortRect}.
     */
    private RectF generateRandomRect(RectF viewPortRect, RectF drawableBounds) {
        float factor = MIN_RECT_FACTOR + (mRandom.nextFloat() / 2);
        float width = factor * viewPortRect.width();
        float height = factor * viewPortRect.height();
        int widthDiff = (int) (viewPortRect.width() - width);
        int heightDiff = (int) (viewPortRect.height() - height);
        int left = widthDiff > 0 ? mRandom.nextInt(widthDiff) : 0;
        int top = heightDiff > 0 ? mRandom.nextInt(heightDiff) : 0;
        return new RectF(left, top, left + width, top + height);
    }
}
