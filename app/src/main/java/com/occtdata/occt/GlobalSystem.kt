package com.occtdata.occt

import android.graphics.Point
import android.widget.ImageView

object GlobalSystem {
    var screenSize : Point? = null;

    var defaultLetterSize = 0.0f;
    var distance = 6.0f

    var astigZoom = 0;

    var changeRateIndex = 0;
    var changeRate = 1;

    var menuSelected = 0;

    public fun outOfScreen(image : ImageView) : Boolean {
        if (screenSize != null){
            val imagePos : IntArray = intArrayOf(0, 0);
            image.getLocationOnScreen(imagePos);

            return imagePos[0] + (image.width/2.0f) >= screenSize!!.x;
        }else {
            return false;
        }
    }
}