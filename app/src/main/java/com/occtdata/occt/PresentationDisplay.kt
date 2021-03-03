package com.occtdata.occt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.commonsware.cwac.preso.PresentationFragment
import kotlinx.android.synthetic.main.presentation_frag.*

class PresentationDisplay : PresentationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.presentation_frag, container, false);
    }

    private var firstStart = true;

    override fun onResume() {
        super.onResume()

        if (firstStart){
            openScreen.setImageResource(R.drawable.ic_mainscreen);
        }

    }

    fun showChart(frag : PresentationFragment){
        openScreen.visibility = View.GONE;

       var bg = childFragmentManager.beginTransaction();
        bg.replace(R.id.presentationLayout, frag);
        bg.commit();
    }

}