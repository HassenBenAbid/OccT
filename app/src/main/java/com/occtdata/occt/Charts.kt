package com.occtdata.occt

import android.annotation.SuppressLint
import android.app.Presentation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import com.commonsware.cwac.preso.PresentationFragment
import kotlinx.android.synthetic.main.menu_frag.*
import kotlin.math.*
import kotlin.random.Random

const val NORMAL_SIZE = 8.7f;
const val MAX_DISTANCE = 60.0f;
const val NORMAL_DISTANCE = 6.0f;

class Charts : PresentationFragment(){

    private val logMarLetters = listOf(
        R.drawable.c_letter,
        R.drawable.d_letter,
        R.drawable.h_letter,
        R.drawable.k_letter,
        R.drawable.n_letter,
        R.drawable.o_letter,
        R.drawable.r_letter,
        R.drawable.s_letter,
        R.drawable.v_letter,
        R.drawable.z_letter
    )

    private val tumblingLetters = listOf(
        R.drawable.tumbling_e,
        R.drawable.tumbling_edown,
        R.drawable.tumbling_eup,
        R.drawable.tumbling_eright
    )

    private val leaSymbols = listOf(
        R.drawable.apple_lea,
        R.drawable.circle_lea,
        R.drawable.square_lea,
        R.drawable.house_lea
    )

    private val kidsAnimals = listOf(
        R.drawable.lion_kids,
        R.drawable.bear_kids,
        R.drawable.kangourou_kids,
        R.drawable.bull_kids,
        R.drawable.chicken_kids,
        R.drawable.duck_kids,
        R.drawable.crocodile_kids,
        R.drawable.cat_kids,
        R.drawable.serpent_kids,
        R.drawable.worm_kids
    )

    private var chartRow = List<ImageView?>(5) {null}
    private var currentLetters : List<ImageView> = emptyList();

    private var currentMode = 0;
    private var currentPage = 0;
    private var currentLetterWidth = 0;

    private val changeRate = 1.26f;
    private var snellenDistance = 0.0f;

    private var singleLetter = false;
    private var duochromeActivated = false;

    private var displayMetrics : DisplayMetrics = DisplayMetrics();

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.menu_frag, container, false);
    }


    override fun onResume() {
        super.onResume();

        chartRow = arrayListOf(letter1, letter2, letter3, letter4, letter5);

        if (chartRow[0] != null){

            currentPage = 0;
            logMarMaxSize();

            randomLogmarRow();

            initDuochrome();

            if (singleLetter) changeToSingleLetter()
            else changeToMultipleLetter()

        }

    }


    //Custom Functions:
    fun logMarMaxSize(){

        GlobalSystem.defaultLetterSize = (NORMAL_SIZE * (GlobalSystem.distance/ NORMAL_DISTANCE));

        setLettersSize(GlobalSystem.defaultLetterSize);
        setDistance();
    }

    private fun randomRow(drawableList: List<Int>){

        var lastIndex : Int = -1;

        for(i in chartRow.indices){
            var currentIndex = Random.nextInt(0, drawableList.size);

            if (currentIndex == lastIndex){
                if (currentIndex != drawableList.size - 1) currentIndex++;
                else currentIndex = 0;
            }

            chartRow[i]!!.setImageResource(drawableList[currentIndex]);
            currentLetters[i]!!.setImageResource(drawableList[currentIndex]);

            lastIndex = currentIndex;
        }
    }

    private fun setLettersSize(size : Float){

        var realSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, size, displayMetrics);
        Log.d("size", realSize.toString());
        currentLetterWidth = realSize.toInt();

        for (element in chartRow){
            element!!.requestLayout();
            element!!.layoutParams.width = currentLetterWidth;
            element!!.layoutParams.height = currentLetterWidth;
        }
    }

    private fun initDuochrome(){
        val halfScreen = GlobalSystem.screenSize!!.x / 2;

        redSplit.layoutParams.width = halfScreen;
        redSplit.layoutParams.height = GlobalSystem.screenSize!!.y;

        greenSplit.layoutParams.width = halfScreen;
        greenSplit.layoutParams.height = GlobalSystem.screenSize!!.y;

        duochromeActivated = false;
        colorVisibility(duochromeActivated);
    }

    @SuppressLint("SetTextI18n")
    private fun setDistance(){

        var currentDistance = NORMAL_DISTANCE;
        if (currentPage > 0) {
            currentDistance *= changeRate.pow(currentPage);
            //currentDistance *= 100.0f;
            currentDistance = round(currentDistance * 10.0f) / 10.0f ;
            if (currentDistance > 10.0f) currentDistance = round(currentDistance);
        }

        snellenDistance = currentDistance;

        var currentLogMar = log10(currentDistance/ NORMAL_DISTANCE);
        currentLogMar = round(currentLogMar * 10.0f) / 10.0f;

        snellenFractionValue.text = "6/$currentDistance";
        logMarValue.text = currentLogMar.toString();
        var decimalDistance = round(NORMAL_DISTANCE/currentDistance * 100.0f) / 100.0f;
        decimalValue.text = decimalDistance.toString();
    }

    private fun outOfScreen(){

        if (!singleLetter){
            if ( currentLetterWidth * 5.0f >= displayMetrics.widthPixels){
                chartRow[4]!!.visibility = View.INVISIBLE;
                chartRow[0]!!.visibility = View.INVISIBLE;

                currentLetters[4].visibility = View.INVISIBLE;
                currentLetters[0].visibility = View.INVISIBLE;
                if (currentLetterWidth * 3.0f >= displayMetrics.widthPixels){
                    chartRow[3]!!.visibility = View.INVISIBLE;
                    chartRow[1]!!.visibility = View.INVISIBLE;

                    currentLetters[3].visibility = View.INVISIBLE;
                    currentLetters[1].visibility = View.INVISIBLE;
                }else {
                    chartRow[3]!!.visibility = View.VISIBLE;
                    chartRow[1]!!.visibility = View.VISIBLE;
                    currentLetters[3].visibility = View.VISIBLE;
                    currentLetters[1].visibility = View.VISIBLE;

                }
            }else {
                chartRow[4]!!.visibility = View.VISIBLE;
                chartRow[0]!!.visibility = View.VISIBLE;

                currentLetters[4].visibility = View.VISIBLE;
                currentLetters[0].visibility = View.VISIBLE;

                chartRow[3]!!.visibility = View.VISIBLE;
                chartRow[1]!!.visibility = View.VISIBLE;
                currentLetters[3].visibility = View.VISIBLE;
                currentLetters[1].visibility = View.VISIBLE;
            }
        }

    }

    private fun refreshCurrentLetterSize(){
        var currentLetterSize = 0.0f;

        if (currentPage > 0){
            currentLetterSize = (GlobalSystem.defaultLetterSize * (currentPage * changeRate));
        }else {
            currentLetterSize = GlobalSystem.defaultLetterSize;
        }

        setLettersSize(currentLetterSize);
        outOfScreen();
    }

    fun nextRow(){

        if (snellenDistance < MAX_DISTANCE){
            currentPage++;

            refreshCurrentLetterSize();
            setDistance();
            randomLogmarRow();
        }

    }

    fun previousRow(){
        if (currentPage > 0){
            currentPage--;

            refreshCurrentLetterSize()
            setDistance();
            randomLogmarRow();

        }
    }

     fun colorVisibility(state : Boolean){
        if (state) {
            redSplit.visibility = View.VISIBLE;
            greenSplit.visibility = View.VISIBLE;
        }else {
            redSplit.visibility = View.INVISIBLE;
            greenSplit.visibility = View.INVISIBLE;
        }

         duochromeActivated = !duochromeActivated;
    }

    fun changeToTumbling(){
        currentMode = TUMBLING_INDEX;
        randomLogmarRow();
    }

    fun changeToLogmar(){
        currentMode = LOGMAR_INDEX;
        randomLogmarRow();
    }

    fun changeToLea(){
        currentMode = LEA_INDEX;
        randomLogmarRow();
    }

    fun changeToKids(){
        currentMode = KIDS_INDEX;
        randomLogmarRow();
    }

    fun randomLogmarRow(){

        if (currentMode == LOGMAR_INDEX) randomRow(logMarLetters);

        else if (currentMode == TUMBLING_INDEX) randomRow(tumblingLetters);

        else if (currentMode == LEA_INDEX) randomRow(leaSymbols);

        else if (currentMode == KIDS_INDEX) randomRow(kidsAnimals);

        refreshCurrentLetterSize();

    }

    fun changeToSingleLetter(){

        if (!singleLetter){
            singleLetter = true;

            for(i in chartRow.indices){
                if (i != 2){
                    chartRow[i]!!.visibility = View.INVISIBLE;
                    currentLetters[i].visibility = View.INVISIBLE;
                }
            }
        }

    }

    fun changeToMultipleLetter(){

        if (singleLetter){
            singleLetter = false;

            for (i in chartRow.indices){
                if (i != 2){
                    chartRow[i]!!.visibility = View.VISIBLE;
                    currentLetters[i].visibility = View.VISIBLE;
                }
            }

            randomLogmarRow();
        }


    }

    fun getActivatedDuochrome() : Boolean{
        return duochromeActivated;
    }

    fun setCurrentScreen(viewScreenList : List<ImageView>){
        currentLetters = viewScreenList;
    }

    fun setCustomDisplay(context: Context, display: Display){
        setDisplay(context, display);
    }

    override fun setDisplay(ctxt: Context?, display: Display?) {
        super.setDisplay(ctxt, display)

        display!!.getMetrics(displayMetrics);
    }

}