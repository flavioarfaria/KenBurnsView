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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class Transition {

    /** The rect the transition will start from. */
    private RectF mSrcRect;

    /** The rect the transition will end at. */
    private RectF mDstRect;

    /** An intermediary rect that changes in every frame according to the transition progress. */
    private final RectF mCurrentRect = new RectF();

    /** Precomputed width difference between {@link #mSrcRect} and {@link #mSrcRect}. */
    private float mWidthDiff;
    /** Precomputed height difference between {@link #mSrcRect} and {@link #mSrcRect}. */
    private float mHeightDiff;
    /** Precomputed X offset between the center points of
     *  {@link #mSrcRect} and {@link #mSrcRect}. */
    private float mCenterXDiff;
    /** Precomputed Y offset between the center points of
     *  {@link #mSrcRect} and {@link #mSrcRect}. */
    private float mCenterYDiff;

    /** The duration of the transition in milliseconds. The default duration is 5000 ms. */
    private long mDuration;

    /** The {@link Interpolator} used to perform the transitions between rects. */
    private Interpolator mInterpolator;


    public Transition(RectF srcRect, RectF dstRect, long duration, Interpolator interpolator) {
        if (!MathUtils.haveSameAspectRatio(srcRect, dstRect)) {
            throw new IncompatibleRatioException();
        }
        mSrcRect = srcRect;
        mDstRect = dstRect;
        mDuration = duration;
        mInterpolator = interpolator;

        // Precomputes a few variables to avoid doing it in onDraw().
        mWidthDiff = dstRect.width() - srcRect.width();
        mHeightDiff = dstRect.height() - srcRect.height();
        mCenterXDiff = dstRect.centerX() - srcRect.centerX();
        mCenterYDiff = dstRect.centerY() - srcRect.centerY();
    }


    /**
     * Gets the rect that will take the scene when a Ken Burns transition starts.
     * @return the rect that starts the transition.
     */
    public RectF getSourceRect() {
        return mSrcRect;
    }


    /**
     * Gets the rect that will take the scene when a Ken Burns transition ends.
     * @return the rect that ends the transition.
     */
    public RectF getDestinyRect() {
        return mDstRect;
    }


    /**
     * Gets the current rect that represents the part of the image to take the scene
     * in the current frame.
     * @param elapsedTime the elapsed time since this transition started.
     */
    public RectF getInterpolatedRect(long elapsedTime) {
        float elapsedTimeFraction = elapsedTime / (float) mDuration;
        float interpolationProgress = Math.min(elapsedTimeFraction, 1);
        float interpolation = mInterpolator.getInterpolation(interpolationProgress);
        float currentWidth = mSrcRect.width() + (interpolation * mWidthDiff);
        float currentHeight = mSrcRect.height() + (interpolation * mHeightDiff);

        float currentCenterX = mSrcRect.centerX() + (interpolation * mCenterXDiff);
        float currentCenterY = mSrcRect.centerY() + (interpolation * mCenterYDiff);

        float left = currentCenterX - (currentWidth / 2);
        float top = currentCenterY - (currentHeight / 2);
        float right = left + currentWidth;
        float bottom = top + currentHeight;

        mCurrentRect.set(left, top, right, bottom);
        return mCurrentRect;
    }


    /**
     * Gets the duration of this transition.
     * @return the duration, in milliseconds.
     */
    public long getDuration() {
        return mDuration;
    }

}
