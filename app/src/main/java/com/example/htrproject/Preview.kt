package com.example.htrproject

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.android.synthetic.main.preview.*
import java.io.IOException

class Preview : Fragment(R.layout.preview)
{
    private val args : PreviewArgs by navArgs()
    var predictedText:String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        val data = args.imageUri
        val imageUri = Uri.parse(data)

        Glide.with(requireContext())
            .load(imageUri)
            .error(R.drawable.ic_gallery)
            .into(capturedImage);



        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
        Log.i("Ayush","Bitmap Created.")



        val image = InputImage.fromBitmap(bitmap, 0)

        val recognizer = TextRecognition.getClient()

        val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    Log.i("Ayush",visionText.text)
                    predictedText = visionText.text
                    Snackbar.make(ok,"Successful .",Snackbar.LENGTH_LONG).show()

                }
                .addOnFailureListener { e ->
                    e.printStackTrace();
                    e.message?.let { Log.i("Ayush", it) }
                    Snackbar.make(ok,"Wait For SomeTime. The app is confguring.",Snackbar.LENGTH_LONG).show()
                }

        ok.setOnClickListener {
            //findNavController().navigate(R.id.action_preview_to_predictedText)
            val args = Bundle()
            args.putString("predictedText", predictedText)
            val newFragment = PredictedText()
            newFragment.arguments = args;
            newFragment.show(requireActivity().supportFragmentManager, "ProjectSEMESTER6")
        }

    }

}