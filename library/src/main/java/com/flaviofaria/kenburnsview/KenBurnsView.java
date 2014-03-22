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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
    private final RectF mDrawableRect = new RectF();

    /** The progress of the animation, in milliseconds. */
    private long mElapsedTime;

    /** The time, in milliseconds, of the last animation frame.
     * This is useful to increment {@link #mElapsedTime} regardless
     * of the amount of time the animation has been paused. */
    private long mLastFrameTime;

    /** Controls whether the the animation is running. */
    private boolean mPaused;

    /** Controls whether the image must be center-cropped or not. */
    private boolean mCenterCrop;


    public KenBurnsView(Context context) {
        this(context, null);
    }


    public KenBurnsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public KenBurnsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Attention to the super call here!
        super.setScaleType(ImageView.ScaleType.MATRIX);
    }


    @Override
    public void setScaleType(ScaleType scaleType) {
        switch (scaleType) {
            case CENTER_CROP:
                mCenterCrop = true;
                break;
            case FIT_CENTER:
                mCenterCrop = false;
                break;
            default:
                String msg = "KenBurnsView only supports ScaleType.CENTER_CROP " +
                        "and ScaleType.FIT_CENTER!";
                throw new UnsupportedOperationException(msg);
        }
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateViewPort(w, h);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (!mPaused) {
            Drawable d = getDrawable();
            updateDrawableBounds();

            // No drawable to animate or bounds are yet to be set? We're done for now.
            if (d != null && !mDrawableRect.isEmpty()) {
                if (mCurrentTrans == null) { // Starting the first transition.
                    startNewTransition();
                }

                if (mCurrentTrans.getDestinyRect() != null) { // If null, it's supposed to stop.
                    mElapsedTime += System.currentTimeMillis() - mLastFrameTime;
                    RectF currentRect = mCurrentTrans.getInterpolatedRect(mElapsedTime);

                    float currentRectRatio = Rects.getRectRatio(currentRect);
                    float drawableRectRatio = Rects.getRectRatio(mDrawableRect);

                    // Scale factor that fits the drawable into the viewport.
                    float drwToVpScale;
                    /* Scale factor that fulfils the viewport
                       with the content inside the current rect. */
                    float currRectToVpScale;
                    /* Scale factor that makes the image appear center-cropped
                       inside the current rect (if enabled). */
                    float centerCropScale = 1;

                    if (drawableRectRatio > currentRectRatio) {
                        drwToVpScale = mViewportRect.width() / mDrawableRect.width();
                        currRectToVpScale = mViewportRect.width() / currentRect.width();
                        if (mCenterCrop) {
                            // Image height after being resized to fit viewport.
                            float drwToVpHeight = mDrawableRect.height() * drwToVpScale;
                            centerCropScale = mViewportRect.height() / drwToVpHeight;
                        }
                    } else {
                        drwToVpScale = mViewportRect.height() / mDrawableRect.height();
                        currRectToVpScale = mViewportRect.height() / currentRect.height();
                        if (mCenterCrop) {
                            // Image width after being resized to fit viewport.
                            float drwToVpWidth = mDrawableRect.width() * drwToVpScale;
                            centerCropScale = mViewportRect.width() / drwToVpWidth;
                        }
                    }
                    currRectToVpScale *= drwToVpScale * centerCropScale;

                    /* Performs matrix transformations to fit the content
                       of the current rect into the entire view. */
                    mMatrix.reset();
                    mMatrix.postTranslate(-d.getIntrinsicWidth() / 2, -d.getIntrinsicHeight() / 2);
                    mMatrix.postScale(currRectToVpScale, currRectToVpScale);
                    mMatrix.postTranslate(currentRect.centerX(), currentRect.centerY());

                    setImageMatrix(mMatrix);
                    postInvalidateDelayed(FRAME_DELAY);

                    // Current transition is over. It's time to start a new one.
                    if (mElapsedTime >= mCurrentTrans.getDuration()) {
                        startNewTransition();
                    }
                } else { // Stopping? A stop event has to be fired.
                    fireTransitionEnd(mCurrentTrans);
                }
            }
            mLastFrameTime = System.currentTimeMillis();
        }
        super.onDraw(canvas);
    }


    /**
     * Generates and starts a transition.
     */
    private void startNewTransition() {
        fireTransitionEnd(mCurrentTrans);
        mCurrentTrans = mTransGen.generateNextTransition(mViewportRect, mDrawableRect);
        mElapsedTime = 0;
        mLastFrameTime = System.currentTimeMillis();
        fireTransitionStart(mCurrentTrans);
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
    }


    /**
     * Updates the viewport rect. This must be called every time the size of this view changes.
     * @param width the new viewport with.
     * @param height the new viewport height.
     */
    private void updateViewPort(float width, float height) {
        mViewportRect.set(0, 0, width, height);
    }


    /**
     * Updates the drawable bounds rect. THis must be called every time the drawable
     * associated to this view changes.
     */
    private void updateDrawableBounds() {
        mDrawableRect.set(getDrawable().getBounds());
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