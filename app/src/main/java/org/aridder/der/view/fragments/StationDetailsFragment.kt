package org.aridder.der.view.fragments

import android.app.Activity
import android.content.Context.SENSOR_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_station_details.*
import com.squareup.seismic.ShakeDetector
import org.aridder.der.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import android.content.Context
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext
import android.content.ContextWrapper


class StationDetailsFragment : SharedFragment(), ShakeDetector.Listener {

    override fun hearShake() {
        Log.d("SHAKE", activity.toString())

        switchToCameraFragment()

    }

    private lateinit var sensorManager: SensorManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val image = loadImageFromStorage("${stationInfoViewModel.stationComplete.value?.id}stationInfo-photo.jpg")

        if(image != null){

            imageView_station_details_image_taken_by_user.setImageBitmap(image)
        }else{
            imageView_station_details_image_taken_by_user.setImageResource(android.R.color.transparent)
        }


        stationInfoViewModel.stationComplete.observe(this, Observer {
            if(it != null){
                textView_item_station_details_current_number_bikes.text = it.stationStatus!!.num_bikes_available.toString()
                textView_fragment_station_details_item_station_details_current_number_locks.text = it.stationStatus.num_docks_available.toString()
                textView_fragment_station_details_item_station_details_station_id.text = it.stationInfo!!.name
                textView_fragment_station_details_item_station_details_stations_name.text = it.stationInfo.name
                textView_fragment_station_details_item_station_details_station_adress.text = it.stationInfo!!.address
                textView_fragment_station_details_item_station_details_avg_bikes_day.text = it.stats!!.averageNumberOfBikesInStationLastDay.toString()
                textView_fragment_station_details_item_station_details_avg_bikes_week.text = it.stats.averageNumberOfBikesInStationLastWeek.toString()
                textView_fragment_station_details_item_station_details_avg_bikes_month.text = it.stats.averageNumberOfBikesInStationLastMonth.toString()
                textView_fragment_station_details_item_station_details_avg_bikes_year.text = it.stats.averageNumberOfBikesInStationLastYear.toString()
                textView_fragment_station_details_item_station_details_percentage_bikes_day.text = it.stats.percentageOfTimeBikeAvailableLastDay.toString()
                textView_fragment_station_details_item_station_details_percentage_bikes_week.text = it.stats.percentageOfTimeBikeAvailableLastWeek.toString()
                textView_fragment_station_details_item_station_details_percentage_bikes_year.text = it.stats.percentageOfTimeBikeAvailableLastMonth.toString()
                textView_fragment_station_details_item_station_details_percentage_bikes_month.text = it.stats.percentageOfTimeBikeAvailableLastYear.toString()


                button_station_details_open_station_in_bysykkel_app.setOnClickListener {button->
                   switchToBysykkelApp(it.stationInfo.station_id)
                }
            }
        })
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_station_details, container, false)
        sensorManager = activity!!.getSystemService(SENSOR_SERVICE) as SensorManager

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val shakeDetector = ShakeDetector(this)
        shakeDetector.start(sensorManager)
    }

    private fun loadImageFromStorage(imageName: String): Bitmap? {
        val cw = ContextWrapper(getApplicationContext())
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("image", Context.MODE_PRIVATE)
        // Create imageDir

        return try {
            val f = File(directory, imageName)
            val b = BitmapFactory.decodeStream(FileInputStream(f))

            b
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }

    }
}
