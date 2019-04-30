package org.aridder.der

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.camerakit.CameraKitView
import kotlinx.android.synthetic.main.fragment_camera.*
import org.aridder.der.view.fragments.SharedFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Bitmap
import android.content.Context.MODE_PRIVATE
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext
import android.content.ContextWrapper
import android.graphics.BitmapFactory


class CameraFragment : SharedFragment() {
    private lateinit var cameraKitView : CameraKitView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        cameraKitView = view.findViewById(R.id.camera)

        val captureButton = view.findViewById<Button>(R.id.button_camera_fragment_take_photo)

        captureButton.setOnClickListener(photoOnClickListener)

        return view
    }


    override fun onStart() {
        super.onStart()
        cameraKitView.onStart()
    }

    override fun onResume() {
        super.onResume()
        cameraKitView.onResume()
    }

    override fun onPause() {
        cameraKitView.onPause()
        super.onPause()
    }

    override fun onStop() {
        cameraKitView.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String {
        val cw = ContextWrapper(getApplicationContext())
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("image", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "${stationInfoViewModel.stationComplete.value?.id}stationInfo-photo.jpg")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }


    val photoOnClickListener: View.OnClickListener = View.OnClickListener {
        val nameOfPhoto = "camera/${stationInfoViewModel.stationComplete.value?.id}stationInfo-photo.jpg"
        Log.d("NAME OF FILE WHEN STORE", nameOfPhoto)
        cameraKitView.captureImage { cameraKitView, capturedImage ->
            saveToInternalStorage(BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.size))
        }
    }
}
