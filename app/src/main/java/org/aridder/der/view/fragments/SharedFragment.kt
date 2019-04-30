package org.aridder.der.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import org.aridder.der.R
import org.aridder.der.viewmodel.StationInfoViewModel
import java.lang.ref.WeakReference

open class SharedFragment : Fragment() {
    protected lateinit var callback: LocationListeningCallback
    lateinit var stationInfoViewModel: StationInfoViewModel
    lateinit var controller: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            stationInfoViewModel = ViewModelProviders.of(this).get(StationInfoViewModel::class.java)
        }
        controller = Navigation.findNavController(this.requireActivity(), R.id.nav_host_fragment)
        callback = LocationListeningCallback(this.activity!!)
    }

    //Here so all fragments has access to position
    inner class LocationListeningCallback internal constructor( activity: FragmentActivity) :
        LocationEngineCallback<LocationEngineResult> {

        private val activityWeakReference: WeakReference<FragmentActivity> = WeakReference(activity)
        var counter = 0

        override fun onSuccess(result: LocationEngineResult) {

            // The LocationEngineCallback interface's method which fires when the device's location has changed.
            if(counter++ < 2){
                stationInfoViewModel.position.value = result.lastLocation
            }
        }

        @SuppressLint("LogNotTimber")
        override fun onFailure(exception: Exception) {
            Log.d("LOCATIONLISTENINGCALLBACK", "ON FAILURE")
            // The LocationEngineCallback interface's method which fires when the device's location can not be captured

        }
    }

    fun switchFromMapFragmentToStationDetailsFragment(){
        controller.navigate(R.id.action_mapFragment_to_stationDetailsFragment)
    }

    fun switchFromStationListToStationDetails(){
        controller.navigate(R.id.action_stationListFragment_to_stationDetailsFragment)
    }

    fun switchToBysykkelApp(id: String) {
        val tripsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("oslobysykkel:stationInfoList/$id"))
        this.startActivity(tripsIntent)
    }

    fun switchToVoiApp() {
        val voiIntent = context?.packageManager?.getLaunchIntentForPackage("io.voiapp.voi")

        when(voiIntent) {
            null -> Toast.makeText(context, "An error occured. Please make sure that you have the Voi app is installed", Toast.LENGTH_LONG).show()
            else -> startActivity(voiIntent)
        }
    }

    fun switchToCameraFragment() {
        val current = controller.currentDestination
        if(current?.label != "fragment_camera"){
                controller.navigate(R.id.action_stationDetailsFragment_to_cameraFragment)
            }
    }

    fun switchToStationDetailsFromCameraFragment(){
        controller.navigate(R.id.action_cameraFragment_to_stationDetailsFragment)
    }
}
