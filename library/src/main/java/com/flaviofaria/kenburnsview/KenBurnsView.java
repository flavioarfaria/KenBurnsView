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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * {@link ImageView} extension that animates its image with the
 * <a href="http://en.wikipedia.org/wiki/Ken_Burns_effect">Ken Burns Effect</a>.
 * @author Flavio Faria
 * @see Transition
 * @see TransitionGenerator
 */
public class KenBurnsView extends ImageView {

    /** Delay between a pair of frames at a 60 FPS frame rate. */
    private static final long FRAME_DELAY = 1000 / 60;

    /** Matrix used to perform all the necessary transition transformations. */
    private final Matrix mMatrix = new Matrix();

    /** The {@link TransitionGenerator} implementation used to perform the transitions between
     *  rects. The default {@link TransitionGenerator} is {@link RandomTransitionGenerator}. */
    private TransitionGenerator mTransGen = new RandomTransitionGenerator();

    /** A {@link KenBurnsView.TransitionListener} to be notified when
     *  a transition starts or ends. */
    private TransitionListener mTransitionListener;

    /** The ongoing transition. */
    private Transition mCurrentTrans;

    /** The rect that holds the bounds of this view. */
    private final RectF mViewportRect = new RectF();
    /** The rect that holds the bounds of the current {@link Drawable}. */
    private RectF mDrawableRect;

    /** The progress of the animation, in milliseconds. */
    private long mElapsedTime;

    /** The time, in milliseconds, of the last animation frame.
     * This is useful to increment {@link #mElapsedTime} regardless
     * of the amount of time the animation has been paused. */
    private long mLastFrameTime;

    /** Controls whether the the animation is running. */
    private boolean mPaused;

    /** Indicates whether the parent constructor was already called.
     * This is needed to distinguish if the image is being set before
     * or after the super class constructor returns. */
    private boolean mInitialized;


    public KenBurnsView(Context context) {
        this(context, null);
    }


    public KenBurnsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public KenBurnsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInitialized = true;
        // Attention to the super call here!
        super.setScaleType(ImageView.ScaleType.MATRIX);
    }


    @Override
    public void setScaleType(ScaleType scaleType) {
        // It'll always be center-cropped by default.
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        /* When not visible, onDraw() doesn't get called,
           but the time elapses anyway. */
        switch (visibility) {
            case VISIBLE:
                resume();
                break;
            default:
                pause();
                break;
        }
    }


    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        handleImageChange();
    }


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        handleImageChange();
    }


    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        handleImageChange();
    }


    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        handleImageChange();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        restart();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable d = getDrawable();
        if (!mPaused && d != null) {
            if (mDrawableRect.isEmpty()) {
                updateDrawableBounds();
            } else if (hasBounds()) {
                if (mCurrentTrans == null) { // Starting the first transition.
                    startNewTransition();
                }

                if (mCurrentTrans.getDestinyRect() != null) { // If null, it's supposed to stop.
                    mElapsedTime += System.currentTimeMillis() - mLastFrameTime;
                    RectF currentRect = mCurrentTrans.getInterpolatedRect(mElapsedTime);

                    float widthScale = mDrawableRect.width() / currentRect.width();
                    float heightScale = mDrawableRect.height() / currentRect.height();
                    // Scale to make the current rect match the smallest drawable dimension.
                    float currRectToDrwScale = Math.min(widthScale, heightScale);
                    // Scale to make the current rect match the viewport bounds.
                    float currRectToVpScale = mViewportRect.width() / currentRect.width();
                    // Combines the two scales to fill the viewport with the current rect.
                    float totalScale = currRectToDrwScale * currRectToVpScale;

                    float translX = totalScale * (mDrawableRect.centerX() - currentRect.left);
                    float translY = totalScale * (mDrawableRect.centerY() - currentRect.top);

                    /* Performs matrix transformations to fit the content
                       of the current rect into the entire view. */
                    mMatrix.reset();
                    mMatrix.postTranslate(-mDrawableRect.width() / 2, -mDrawableRect.height() / 2);
                    mMatrix.postScale(totalScale, totalScale);
                    mMatrix.postTranslate(translX, translY);

                    setImageMatrix(mMatrix);

                    // Current transition is over. It's time to start a new one.
                    if (mElapsedTime >= mCurrentTrans.getDuration()) {
                        fireTransitionEnd(mCurrentTrans);
                        startNewTransition();
                    }
                } else { // Stopping? A stop event has to be fired.
                    fireTransitionEnd(mCurrentTrans);
                }
            }
            mLastFrameTime = System.currentTimeMillis();
            postInvalidateDelayed(FRAME_DELAY);
        }
        super.onDraw(canvas);
    }


    /**
     * Generates and starts a transition.
     */
    private void startNewTransition() {
        if (!hasBounds()) {
            throw new UnsupportedOperationException("Can't start transition if the " +
                                                    "drawable has no bounds!");
        }
        mCurrentTrans = mTransGen.generateNextTransition(mDrawableRect, mViewportRect);
        mElapsedTime = 0;
        mLastFrameTime = System.currentTimeMillis();
        fireTransitionStart(mCurrentTrans);
    }


    /**
     * Creates a new transition and starts over.
     */
    public void restart() {
        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) {
            throw new UnsupportedOperationException("Can't call restart() when view area is zero!");
        }

        updateViewport(width, height);
        updateDrawableBounds();

        if (hasBounds()) {
            startNewTransition();
        }
    }


    /**
     * Checks whether this view has bounds.
     * @return
     */
    private boolean hasBounds() {
        return !mViewportRect.isEmpty();
    }


    /**
     * Fires a start event on {@link #mTransitionListener};
     * @param transition the transition that just started.
     */
    private void fireTransitionStart(Transition transition) {
        if (mTransitionListener != null && transition != null) {
            mTransitionListener.onTransitionStart(transition);
        }
    }


    /**
     * Fires an end event on {@link #mTransitionListener};
     * @param transition the transition that just ended.
     */
    private void fireTransitionEnd(Transition transition) {
        if (mTransitionListener != null && transition != null) {
            mTransitionListener.onTransitionEnd(transition);
        }
    }


    /**
     * Sets the {@link TransitionGenerator} to be used in animations.
     * @param transgen the {@link TransitionGenerator} to be used in animations.
     */
    public void setTransitionGenerator(TransitionGenerator transgen) {
        mTransGen = transgen;
        if (hasBounds()) {
            startNewTransition();
        }
    }


    /**
     * Updates the viewport rect. This must be called every time the size of this view changes.
     * @param width the new viewport with.
     * @param height the new viewport height.
     */
    private void updateViewport(float width, float height) {
        mViewportRect.set(0, 0, width, height);
    }


    /**
     * Updates the drawable bounds rect. This must be called every time the drawable
     * associated to this view changes.
     */
    private void updateDrawableBounds() {
        if (mDrawableRect == null) {
            mDrawableRect = new RectF();
        }
        Drawable d = getDrawable();
        if (d != null) {
            mDrawableRect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }
    }


    /**
     * This method is called every time the underlying image
     * is changed.
     */
    private void handleImageChange() {
        updateDrawableBounds();
        /* Don't start a new transition if this event
         was fired during the super constructor execution.
         The view won't be ready at this time. Also,
         don't start it if this view size is still unknown. */
        if (mInitialized && hasBounds()) {
            startNewTransition();
        }
    }


    public void setTransitionListener(TransitionListener transitionListener) {
        mTransitionListener = transitionListener;
    }


    /**
     * Pauses the Ken Burns Effect animation.
     */
    public void pause() {
        mPaused = true;
    }


    /**
     * Resumes the Ken Burns Effect animation.
     */
    public void resume() {
        mPaused = false;
        // This will make the animation to continue from where it stopped.
        mLastFrameTime = System.currentTimeMillis();
        invalidate();
    }


    /**
     * A transition listener receives notifications when a transition starts or ends.
     */
    public interface TransitionListener {
        /**
         * Notifies the start of a transition.
         * @param transition the transition that just started.
         */
        public void onTransitionStart(Transition transition);

        /**
         * Notifies the end of a transition.
         * @param transition the transition that just ended.
         */
        public void onTransitionEnd(Transition transition);
    }
}
