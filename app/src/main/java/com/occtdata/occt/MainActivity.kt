package com.occtdata.occt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.commonsware.cwac.preso.PresentationHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


const val LOGMAR_INDEX = 0;
const val TUMBLING_INDEX = 1;
const val LEA_INDEX = 2;
const val KIDS_INDEX = 3;
const val ASTIGMATISM_INDEX = 4;


class MainActivity : AppCompatActivity(), PresentationHelper.Listener,
    DisplayManager.DisplayListener{

    private val fragmentManager = supportFragmentManager

    private val chartFragment = Charts();
    private val astigFragment = AstigChart();
    private val saveFileName = "prefs";

    private var currentChart = -1;

    private lateinit var helper : PresentationHelper;
    private lateinit var displayManager: DisplayManager;

    private var secondDisplayer : Boolean = false;
    private var alertRate = ChoiceDialog();

    private lateinit var screenViewLetters : List<ImageView>;

    private var presentationDisplay = PresentationDisplay();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPrefs();

        screenViewLetters = listOf(viewLetter1, viewLetter2, viewLetter3, viewLetter4, viewLetter5);
        chartFragment.setCurrentScreen(screenViewLetters);

        helper = PresentationHelper(this, this);

        displayManager = this.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager;
        displayManager.registerDisplayListener(this, null);

        secondDisplayer = displayManager.displays.size > 1;

        if (secondDisplayer) {
            initSecondDisplayer();
            displayWarning.visibility = View.GONE;
        }else {
            displayWarning.visibility = View.VISIBLE;
            changeMainScreen();
        }

        menuInteraction();
        changeMainScreen();

    }

    private fun initSecondDisplayer(){
        GlobalSystem.screenSize = getScreenSize();

        chartFragment.setDisplay(this, displayManager.displays[1]);
        astigFragment.setDisplay(this, displayManager.displays[1]);

        presentationDisplay.setDisplay(this, displayManager.displays[1]);

        GlobalScope.launch {

            var tr = fragmentManager.beginTransaction();
            tr.add(presentationDisplay, "astig");
            tr.commit();
        }

    }

    private fun menuInteraction(){


        buttonLogMar.setOnClickListener(){
            mainMenuSelected();
            loadLogMarChart();
            buttonClicked(buttonLogMar);
        }

        buttonTumbling.setOnClickListener(){
            mainMenuSelected();
            loadTumblingE();
            buttonClicked(buttonTumbling);
        }
        buttonLea.setOnClickListener(){
            mainMenuSelected();
            loadLeaSymbols();
            buttonClicked(buttonLea);
        }

        buttonAstig.setOnClickListener(){
            mainMenuSelected();
            loadAstigmastism();
            buttonClicked(buttonAstig);
        }

        kidButton.setOnClickListener(){
            mainMenuSelected();
            loadKid();
            buttonClicked(kidButton);
        }

        duochromeButton.setOnClickListener(){
            chartFragment.colorVisibility(chartFragment.getActivatedDuochrome());
            buttonClicked(duochromeButton);
        }

        refreshButton.setOnClickListener(){
            chartFragment.randomLogmarRow();
            buttonClicked(refreshButton);
        }

        singleletterButton.setOnClickListener(){
            chartFragment.changeToSingleLetter();
            buttonClicked(singleletterButton);
        }

        multipleletterButton.setOnClickListener(){
            chartFragment.changeToMultipleLetter();
            buttonClicked(multipleletterButton);
        }

        uprowButton.setOnClickListener(){
            if (currentChart <= KIDS_INDEX && currentChart != -1) chartFragment.nextRow();
            else if (currentChart == ASTIGMATISM_INDEX) astigFragment.rotate(1.0f);

            buttonClicked(uprowButton);
        }

        downRowButton.setOnClickListener(){
            if (currentChart <= KIDS_INDEX && currentChart != -1) chartFragment.previousRow();
            else if (currentChart == ASTIGMATISM_INDEX) astigFragment.rotate(-1.0f);
            buttonClicked(downRowButton);
        }

        castButton.setOnClickListener(){
            startActivity(Intent("android.settings.CAST_SETTINGS"));
            buttonClicked(castButton);
        }

        distanceInput.setOnClickListener(){
            runOnUiThread(Runnable {
                alertRate.show(fragmentManager, "distance");
            })
        }

        plusButton.setOnClickListener(){
            astigFragment.zoom(1.0f);
            buttonClicked(plusButton);
        }

        minusButton.setOnClickListener(){
            astigFragment.zoom(-1.0f);
            buttonClicked(minusButton);
        }

    }

    private fun buttonClicked(button: ImageButton){
        val anim = AnimationUtils.loadAnimation(this, R.anim.zoomout);
        button.startAnimation(anim);
    }

    private fun changeMainScreen(){

        if (currentChart <= KIDS_INDEX && currentChart != -1){

            duochromeButton.visibility = View.VISIBLE;
            refreshButton.visibility = View.VISIBLE;
            singleletterButton.visibility = View.VISIBLE;
            multipleletterButton.visibility = View.VISIBLE;
            distanceInput.visibility = View.VISIBLE;

            minusButton.visibility = View.GONE;
            plusButton.visibility = View.GONE;

            uprowButton.visibility = View.VISIBLE;
            downRowButton.visibility = View.VISIBLE;

            viewScreen.visibility = View.VISIBLE;
        }else if (currentChart == ASTIGMATISM_INDEX){
            duochromeButton.visibility = View.GONE;
            refreshButton.visibility = View.GONE;
            singleletterButton.visibility = View.GONE;
            multipleletterButton.visibility = View.GONE;
            distanceInput.visibility = View.GONE;

            minusButton.visibility = View.VISIBLE;
            plusButton.visibility = View.VISIBLE;

            uprowButton.visibility = View.VISIBLE;
            downRowButton.visibility = View.VISIBLE;

            viewScreen.visibility = View.GONE;
        }else if (currentChart == -1){
            duochromeButton.visibility = View.GONE;
            refreshButton.visibility = View.GONE;
            singleletterButton.visibility = View.GONE;
            multipleletterButton.visibility = View.GONE;
            distanceInput.visibility = View.GONE;

            minusButton.visibility = View.GONE;
            plusButton.visibility = View.GONE;

            uprowButton.visibility = View.GONE;
            downRowButton.visibility = View.GONE;

            viewScreen.visibility = View.GONE;
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) fullScreen()
    }

    override fun onResume() {
        super.onResume();
        helper.onResume();

        if (!secondDisplayer && displayManager.displays.size > 1) {
            secondDisplayer = true;
            initSecondDisplayer();
        }
    }

    override fun onStop() {
        savePrefs();

        helper.onPause();
        super.onStop();
    }

    private fun getScreenSize() : Point {
        val display : Display = displayManager.displays[1];
        var size : Point = Point();
        display.getRealSize(size);

        return size;
    }

    private fun fullScreen(){
       window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY + View.SYSTEM_UI_FLAG_FULLSCREEN
                + View.SYSTEM_UI_FLAG_HIDE_NAVIGATION + View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN + View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private fun loadLogMarChart(){

        if (currentChart != LOGMAR_INDEX  && secondDisplayer){


            if (currentChart > KIDS_INDEX || currentChart == -1) presentationDisplay.showChart(chartFragment);
            if (currentChart != -1) chartFragment.changeToLogmar();

            currentChart = LOGMAR_INDEX;
            changeMainScreen();

        }

    }

    private fun loadTumblingE(){
        if (currentChart != TUMBLING_INDEX && secondDisplayer){

            if (currentChart > KIDS_INDEX || currentChart == -1) presentationDisplay.showChart(chartFragment);


            currentChart = TUMBLING_INDEX;
            changeMainScreen();
            chartFragment.changeToTumbling();
        }
    }

    private fun loadKid(){
        if (currentChart != KIDS_INDEX && secondDisplayer){

            if (currentChart > KIDS_INDEX || currentChart == -1) presentationDisplay.showChart(chartFragment);

            currentChart = KIDS_INDEX;
            changeMainScreen();
            chartFragment.changeToKids();
        }
    }

    private fun loadLeaSymbols(){
        if (currentChart != LEA_INDEX && secondDisplayer){

            if (currentChart > KIDS_INDEX || currentChart == -1) presentationDisplay.showChart(chartFragment);

            currentChart = LEA_INDEX;
            changeMainScreen();
            chartFragment.changeToLea();
        }
    }

    private fun loadAstigmastism(){
        if (currentChart != ASTIGMATISM_INDEX && secondDisplayer){

            presentationDisplay.showChart(astigFragment);

            currentChart = ASTIGMATISM_INDEX;
            changeMainScreen();

        }
    }

    private fun savePrefs(){

        val saveFile : SharedPreferences = this.getSharedPreferences(saveFileName, 0);
        val saveEditor : SharedPreferences.Editor = saveFile.edit();
        saveEditor.putFloat("distance", GlobalSystem.distance);
        saveEditor.putInt("zoom", GlobalSystem.astigZoom);
        saveEditor.apply();
    }

    private fun loadPrefs(){

        val saveFile : SharedPreferences = this.getSharedPreferences(saveFileName, 0);
        GlobalSystem.distance = saveFile.getFloat("distance", NORMAL_DISTANCE);
        GlobalSystem.astigZoom = saveFile.getInt("zoom", 0);

        distanceInput.text = (GlobalSystem.distance * 100.0f).toString() + " Centimeters";
    }

    private fun mainMenuSelected(){

        /*val menuSize : Int = menuTable.childCount;

        for (i in 0 until menuSize){
           menuTable.getChildAt(i).scaleX = 1f;
           menuTable.getChildAt(i).scaleY = 1f;
        }

        GlobalScope.launch {

            delay(190);

            menuTable.getChildAt(currentChart).scaleX -= 0.1f;
            menuTable.getChildAt(currentChart).scaleY -= 0.1f;
        }*/

    }

    override fun onDisplayAdded(p0: Int) {
        if (!secondDisplayer && displayManager.displays.size > 1) displayWarning.visibility = View.GONE;
    }

    override fun onDisplayRemoved(p0: Int) {
        if (displayManager.displays.size <= 1){
            secondDisplayer = false;
            currentChart = -1;
            changeMainScreen();
            displayWarning.visibility = View.VISIBLE;
        }
    }

    override fun clearPreso(switchToInline: Boolean) {

    }

    override fun showPreso(display: Display?) {

    }

    override fun onDisplayChanged(p0: Int) {

    }

}