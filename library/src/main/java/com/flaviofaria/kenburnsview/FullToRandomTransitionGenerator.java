/*
 * Copyright 2015 Flavio Faria and Eric Sieg
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

import java.util.Random;

/**
 * Transition generator meant to be used in a {@link KenBurnsView} and most likely in a slideshow.
 * The generator starts full zoomed out and then does a ken burns animation moving in towards a
 * random part of the image. This does not crop the image and thus works well with an image view
 * that is using a 'center inside' approach instead of a 'center crop' approach.
 * @author Eric Sieg
 * @author Flavio Faria
 * @see KenBurnsView
 * @see TransitionGenerator
 */
public class FullToRandomTransitionGenerator implements TransitionGenerator {

    /** Default value for the transition duration in milliseconds. */
    public static final int DEFAULT_TRANSITION_DURATION = 10000;

    /** Minimum rect dimension factor, according to the maximum one. */
    private static final float MIN_RECT_FACTOR = 0.85f;

    /** Random object used to generate arbitrary rects. */
    private final Random mRandom = new Random(System.currentTimeMillis());

    /** The duration, in milliseconds, of each transition. */
    private long mTransitionDuration;

    /** The {@link Interpolator} to be used to create transitions. */
    private Interpolator mTransitionInterpolator;

    /** The last generated transition. */
    private Transition mLastGenTrans;

    /** The bounds of the drawable when the last transition was generated. */
    private RectF mLastDrawableBounds;


    public FullToRandomTransitionGenerator() {
        this(DEFAULT_TRANSITION_DURATION, new AccelerateDecelerateInterpolator());
    }


    public FullToRandomTransitionGenerator(long transitionDuration, Interpolator transitionInterpolator) {
        setTransitionDuration(transitionDuration);
        setTransitionInterpolator(transitionInterpolator);
    }

    public boolean isCroppingImage() {
        return false;
    }

    @Override
    public Transition generateNextTransition(RectF drawableBounds, RectF viewport) {
        RectF srcRect = generateFullBalancedRect(drawableBounds, viewport);
        RectF dstRect = generateRandomRect(drawableBounds, viewport);

        mLastGenTrans = new Transition(srcRect, dstRect, mTransitionDuration,
                mTransitionInterpolator);

        mLastDrawableBounds = drawableBounds;

        return mLastGenTrans;
    }


    /**
     * Generates a random rect that can be fully contained within {@code drawableBounds}.
     * The dimensions of this random rect won't be higher than the largest rect with the same
     * aspect ratio of {@code viewportRect} that {@code drawableBounds} can contain.
     * They also won't be lower than the dimensions of this upper rect limit weighted
     * by {@code MIN_RECT_FACTOR}.
     * @param drawableBounds the bounds of the drawable that will be zoomed and panned.
     * @param viewportRect the bounds of the view that the drawable will be shown inside.
     * @return an arbitrary generated rect that will be contained within {@code drawableBounds}.
     */
    private RectF generateRandomRect(RectF drawableBounds, RectF viewportRect) {

        float widthRatio =  viewportRect.width() / drawableBounds.width();
        float heightRatio = viewportRect.height() / drawableBounds.height();
        float smallestRatio = widthRatio > heightRatio ? heightRatio : widthRatio;

        float randomFloat = MathUtils.truncate(mRandom.nextFloat(), 2);
        float factor = MIN_RECT_FACTOR + ((1 - MIN_RECT_FACTOR) * randomFloat);

        float width = drawableBounds.width() * smallestRatio * factor;
        float height = drawableBounds.height() * smallestRatio * factor;
        int widthDiff = (int) (drawableBounds.width() - width);
        int heightDiff = (int) (drawableBounds.height() - height);
        int left = widthDiff > 0 ? mRandom.nextInt(widthDiff) : 0;
        int top = heightDiff > 0 ? mRandom.nextInt(heightDiff) : 0;
        return new RectF(left, top, left + width, top + height);
    }

    private RectF generateFullBalancedRect(RectF drawableBounds, RectF viewportRect) {

        float widthRatio =  viewportRect.width() / drawableBounds.width();
        float heightRatio = viewportRect.height() / drawableBounds.height();
        float smallestRatio = widthRatio > heightRatio ? heightRatio : widthRatio;

        float width = drawableBounds.width() * smallestRatio;
        float height = drawableBounds.height() * smallestRatio;
        int left = 0;
        int top = 0;
        return new RectF(left, top, left + width, top + height);
    }


    /**
     * Sets the duration, in milliseconds, for each transition generated.
     * @param transitionDuration the transition duration.
     */
    public void setTransitionDuration(long transitionDuration) {
        mTransitionDuration = transitionDuration;
    }


    /**
     * Sets the {@link Interpolator} for each transition generated.
     * @param interpolator the transition interpolator.
     */
    public void setTransitionInterpolator(Interpolator interpolator) {
        mTransitionInterpolator = interpolator;
    }
}
