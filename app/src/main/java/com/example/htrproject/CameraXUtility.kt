package com.example.htrproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.camerax.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraXUtility : Fragment(R.layout.camerax)
{
    private lateinit var cameraSelector: CameraSelector
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var flashMode: Int = ImageCapture.FLASH_MODE_ON
    private val PICK_IMAGE = 100
    var imageUri: Uri? = null





    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onStart()
    {
        super.onStart()

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener(Runnable {
            try{
                cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)}
            catch ( e: InterruptedException)
            {
                val text = "Please provide Camera access to this app."
                val duration = Toast.LENGTH_LONG
                val toast = Toast.makeText(activity,text,duration)
                toast.show()
            }
        }, ContextCompat.getMainExecutor(activity))


        button.setOnClickListener {
            imageCapture()
        }



        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                R.id.more -> {
                    // Handle more item (inside overflow menu) press
                    if(flashMode==ImageCapture.FLASH_MODE_ON)
                    {
                        flashMode=ImageCapture.FLASH_MODE_OFF
                        menuItem.setIcon(R.drawable.ic_baseline_flash_off_24)
                    }

                    else
                    {
                        flashMode=ImageCapture.FLASH_MODE_ON
                        menuItem.setIcon(R.drawable.ic_baseline_flash_on_24)
                    }

                    true
                }


                R.id.gallery -> {
                    openGallery()
                    true
                }


                else -> false
            }
        }
    }


    //setting the preview for the camera
    @SuppressLint("UnsafeExperimentalUsageError")
    fun bindPreview(cameraProvider: ProcessCameraProvider)
    {
        val preview : Preview = Preview.Builder()
            .build()

        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)


        //binding the camera to the lifecycle of this fragment(CameraXUtility)
        val camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)


    }


    // defines what happens when the image is captured.
    private fun imageCapture()
    {
        val imageCapture = ImageCapture.Builder()
            .setTargetRotation(requireView().display.rotation)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setFlashMode(flashMode)
            .build()


        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageCapture)

        capturingUtility(imageCapture,cameraProvider)
    }


    private fun capturingUtility(imageCapture:ImageCapture,cameraProvider: ProcessCameraProvider)
    {
        val photoFile = File(getDirectory.getOutputDirectory(requireContext()), SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    val savedUri = Uri.fromFile(photoFile)
                    val data = savedUri.toString()
                    val action = CameraXUtilityDirections.actionCameraXUtilityToPreview(data)

                    findNavController().navigate(action)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.i("Ayush", "Photo capture failed: ${exception.message}", exception)
                }
            }
        )

    }



    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data

            val data = imageUri.toString()
            val action = CameraXUtilityDirections.actionCameraXUtilityToPreview(data)
            cameraProvider.unbindAll()
            findNavController().navigate(action)
            /*imageView.setImageURI(imageUri)
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val imageProcessor = ImageProcessor.Builder()
                    .add(ResizeOp(100, 100, ResizeOp.ResizeMethod.BILINEAR))
                    .build()

            var tImage = TensorImage(DataType.FLOAT32)
            tImage.load(bitmap)
            tImage = imageProcessor.process(tImage)
            interpreter1(tImage.buffer)*/
        }
    }




    override fun onPause() {
        super.onPause()
        Log.i("Ayush","OnPause called.")
    }

    override fun onStop() {
        super.onStop()
        cameraProvider.unbindAll()
        Log.i("Ayush","OnStop called.")
    }


}