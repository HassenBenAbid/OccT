package com.occtdata.occt

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.commonsware.cwac.preso.PresentationFragment
import kotlinx.android.synthetic.main.asitgmastism_frag.*
import kotlin.math.sign

const val ROTATING_RATE = 3.0f;
const val ZOOM_RATE = 5;
const val MAX_ZOOM = 40;
const val MIN_ZOOM = -35;

class AstigChart : PresentationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.asitgmastism_frag, container, false);
    }

    private var currentRotation = -20.0f;
    private lateinit var currentContext : Context;

    override fun onResume() {
        super.onResume();

        init();
    }

    private fun init(){

        backgroundFan.setImageResource(R.drawable.background_fan);
        rotatingFan.setImageResource(R.drawable.foreground_fan);

        changeSize(GlobalSystem.astigZoom, backgroundFan);
        changeSize((GlobalSystem.astigZoom/2.0f).toInt(), rotatingFan);
    }

    private fun rotateFan(value : Float){

        rotatingFan.rotation = value;
    }

    private fun changeSize(value : Int, image: ImageView){
        image.requestLayout();
        image.layoutParams.width += value;
        image.layoutParams.height += value;
    }

    fun rotate(direction : Float){
        val dir = sign(direction);
        currentRotation += dir * ROTATING_RATE;

        if (currentRotation > 200.0f) currentRotation = -20.0f;
        else if (currentRotation < -20.0f) currentRotation = 200.0f;

        rotateFan(currentRotation);
    }

    fun zoom(direction : Float){
        val dir = sign(direction);

        if ((GlobalSystem.astigZoom < MAX_ZOOM || (GlobalSystem.astigZoom >= MAX_ZOOM && dir < 0)) &&
            (GlobalSystem.astigZoom > MIN_ZOOM) || GlobalSystem.astigZoom <= MIN_ZOOM && dir > 0){

            changeSize((dir * ZOOM_RATE).toInt(), backgroundFan);
            changeSize((dir * ZOOM_RATE).toInt(), rotatingFan);

            GlobalSystem.astigZoom += (dir * ZOOM_RATE).toInt();
        }

        Log.d("zoom", GlobalSystem.astigZoom.toString());
    }

    fun setCustomDisplay(chosenContext : Context, chosenDisplay : Display){
        currentContext = chosenContext;
        setDisplay(chosenContext, chosenDisplay);
    }



}