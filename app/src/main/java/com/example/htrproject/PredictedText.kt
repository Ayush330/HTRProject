package com.example.htrproject


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.predicted_text.*

class PredictedText() : BottomSheetDialogFragment()
{
    lateinit var pview: View;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        pview = inflater.inflate(R.layout.predicted_text, container, false);
        return pview;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        val mArgs = arguments
        val predictedText = mArgs!!.getString("predictedText")
        predict.text = predictedText
    }
}