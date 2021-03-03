package com.occtdata.occt

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_frag.*

class ChoiceDialog : DialogFragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it);
            builder.setTitle("User Distance");
            val input : EditText = EditText(this.context);
            input.inputType = InputType.TYPE_CLASS_NUMBER;
            input.textSize = 15.0f;
            input.setText((GlobalSystem.distance * 100).toString());
            builder.setView(input);

            builder.setPositiveButton("OK"){dialog : DialogInterface, which : Int ->

                    var distance = input.text.toString().toFloat();
                    if (distance > 600) distance = 600.0f;
                    else if (distance < 100) distance = 100.0f;

                    GlobalSystem.distance = distance / 100.0f
                    GlobalSystem.defaultLetterSize = (NORMAL_SIZE * (GlobalSystem.distance/ NORMAL_DISTANCE));
                    it.distanceInput.text =  "${distance.toInt()} centimeters";

            }

            builder.setNeutralButton("Cancel"){dialog : DialogInterface, which : Int ->
                dialog.cancel();
            }

            val dialog = builder.create();

            return dialog;
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
    }


}