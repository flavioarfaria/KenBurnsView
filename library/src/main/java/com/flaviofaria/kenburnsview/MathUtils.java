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

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Helper class to perform math computations.
 */
public final class MathUtils {

    /**
     * Truncates a float number {@code f} to {@code decimalPlaces}.
     * @param f the number to be truncated.
     * @param decimalPlaces the amount of decimals that {@code f}
     * will be truncated to.
     * @return a truncated representation of {@code f}.
     */
    protected static float truncate(float f, int decimalPlaces) {
        float decimalShift = (float) Math.pow(10, decimalPlaces);
        return Math.round(f * decimalShift) / decimalShift;
    }


    /**
     * Checks whether two {@link RectF} have the same aspect ratio.
     * @param r1 the first rect.
     * @param r2  the second rect.
     * @return {@code true} if both rectangles have the same aspect ratio,
     * {@code false} otherwise.
     */
    protected static boolean haveSameAspectRatio(RectF r1, RectF r2) {
        // Reduces precision to avoid problems when comparing aspect ratios.
        float srcRectRatio = MathUtils.truncate(MathUtils.getRectRatio(r1), 3);
        float dstRectRatio = MathUtils.truncate(MathUtils.getRectRatio(r2), 3);
        
        // Compares aspect ratios that allows for a tolerance range of [0, 0.01] 
        return (Math.abs(srcRectRatio-dstRectRatio) <= 0.01f);
    }


    /**
     * Computes the aspect ratio of a given rect.
     * @param rect the rect to have its aspect ratio computed.
     * @return the rect aspect ratio.
     */
    protected static float getRectRatio(RectF rect) {
        return rect.width() / rect.height();
    }

    /**
     * Computes the rect inside the given rect for the aspect ratio.
     * @param a_currentRect the rect to find the inside rect of.
     * @param a_desiredAspect the aspect ratio to use
     * @return the rect aspect ratio.
     */
    protected static RectF getInsideRect(RectF a_currentRect, float a_desiredAspect)
    {
        /*
            Example:

                a_currentRect.left = 0;
                a_currentRect.right = 4;
                a_currentRect.top = 0;
                a_currentRect.bottom = 3;

                a_desiredAspect = 1.777777777777778f;

                Aspect ration of a 16:9 Rect = 1.777777777777778
                Inverse aspect of a 16:9 Rect = 0.5625

                Given a 4:3 Rect at the origin the result would be

                l_currentAspect = 1.333333333333333

                l_newWidth = 4.0f

                l_newHeight = 3.0f  * 0.5625
         */


        float l_currentAspect = a_currentRect.width() / a_currentRect.height();

        float l_inverseAspect = 1.0f / a_desiredAspect;

        float l_newWidth = 0.0f;
        float l_newHeight = 0.0f;

        RectF l_newRect = new RectF();

        if (a_desiredAspect > 1.0f)
        {
            l_newWidth = a_currentRect.width();
            l_newHeight = a_currentRect.width()  * l_inverseAspect;
        }
        else
        {
            l_newWidth = a_currentRect.height() * l_inverseAspect;
            l_newHeight = a_currentRect.height();
        }

        l_newRect.left = a_currentRect.centerX() - (l_newWidth / 2.0f);
        l_newRect.right = a_currentRect.centerX() + (l_newWidth / 2.0f);
        l_newRect.top = a_currentRect.centerY() - (l_newHeight / 2.0f);
        l_newRect.bottom = a_currentRect.centerY() + (l_newHeight / 2.0f);

        float l_newRectAspect = MathUtils.getRectRatio(l_newRect);

        return l_newRect;
    }
}
