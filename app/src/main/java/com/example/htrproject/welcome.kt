package com.example.htrproject


import android.Manifest
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.welcomee.*


class welcome : Fragment(R.layout.welcomee)
{
    lateinit var animation:AnimationDrawable
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        imageView2.setBackgroundResource(R.drawable.spin)
        animation = imageView2.background as AnimationDrawable
        animation.start()

        start.setOnClickListener {
            requestContactsPermission();
        }
    }

    private fun requestContactsPermission() {
        Dexter.withActivity(requireActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?)
                    {
                        findNavController().navigate(R.id.action_welcome_to_cameraXUtility)
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?)
                    {
                        token?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Log.i("Denied","Permission Denied")
                        val newFragment = dialog()
                        newFragment.show((activity as FragmentActivity).supportFragmentManager, "Permanently Denied")
                    }
                } )
                .check()
    }

    override fun onPause()
    {
        super.onPause()
        animation.stop()
    }
}