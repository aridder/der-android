package org.aridder.der

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.squareup.seismic.ShakeDetector


class MainActivity : FragmentActivity() {
    lateinit var controller: NavController
    lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        controller = Navigation.findNavController(this, R.id.nav_host_fragment)
        (navigation as BottomNavigationView).setupWithNavController(controller)

        //val shakeDetector = ShakeDetector(this)

        //Stetho is an open source library made by Facebook for debugging e.g. sqlite local storage.
        Stetho.initializeWithDefaults(this)
    }

}
