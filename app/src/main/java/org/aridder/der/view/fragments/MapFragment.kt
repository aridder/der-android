package org.aridder.der.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.*
import org.aridder.der.BuildConfig
import org.aridder.der.R
import org.aridder.der.model.StationComplete
import org.aridder.der.model.Voi
import org.aridder.der.viewmodel.BysykkelViewModel
import org.aridder.der.viewmodel.VoiViewModel

class MapFragment : SharedFragment(), PermissionsListener {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var mContext: Context
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var locationEngine: LocationEngine
    private lateinit var locationComponent: LocationComponent
    private lateinit var bysykkelViewModel: BysykkelViewModel
    private lateinit var voiViewModel: VoiViewModel
    private var zoomLevel: Double = -1.0


    val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

    enum class MarkerType{
        VOI,
        BYSYKKEL
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        initMapWithOnClicks()
    }

    private fun initMapWithOnClicks() {
        mapView.getMapAsync {
            map = it
            it.setStyle(Style.LIGHT) { style -> enableLocation(style) }
            zoomLevel = it.cameraPosition.zoom

            map.setOnMarkerClickListener { marker ->

                if(marker.title == "bysykkel"){
                    stationInfoViewModel.stationComplete.value = getStationInfo(marker.snippet.toLong())

                    val transaction = activity!!.supportFragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(
                            R.id.station_info_fragment_holder,
                            StationInfoMapFragment()
                        )
                    transaction.commit()

                }

                if(marker.title == "voi"){

                    stationInfoViewModel.voi.value = getVoiBikeInfo(marker.snippet)

                    val transaction = activity!!.supportFragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(
                            R.id.station_info_fragment_holder,
                            VoiInfoMapFragment()
                        )
                    transaction.commit()

                }

                true
            }

            map.addOnMapClickListener {

                if (activity!!.supportFragmentManager.findFragmentById(R.id.station_info_fragment_holder) != null) {
                    activity!!.supportFragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .remove(activity!!.supportFragmentManager.findFragmentById(R.id.station_info_fragment_holder)!!)
                        .commit()
                }
                true
            }
            initObservers()
        }
    }

    private fun getVoiBikeInfo(snippet: String): Voi? {
        return voiViewModel.voiBike.value?.find {
            it.id == snippet
        }
    }

    private fun getStationInfo(id: Long): StationComplete? {
        return bysykkelViewModel.stationCompleteList.value!!.firstOrNull {
            it.stationInfo?.station_id == id.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bysykkelViewModel = ViewModelProviders.of(this).get(BysykkelViewModel::class.java)
        voiViewModel = ViewModelProviders.of(this).get(VoiViewModel::class.java)

        mContext = inflater.context
        Mapbox.getInstance(mContext, BuildConfig.MAPBOX_KEY)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    fun enableLocation(style: Style){
        Log.d("MAPFRAGMENT", "enableLocation function invoked")
        if(PermissionsManager.areLocationPermissionsGranted(mContext)){
            initilizeLocationEngine()
            initilizieLocationLayer(style)

            drawBysykkelMarkers()
            drawVoiBikesMarkers()

        }else{
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this.activity)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        //Dialog why need to grant access
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted){
            Log.d("MAPFRAGMENT", "On Permisson Result = granted")
            //enableLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun initilizeLocationEngine(){
        locationEngine = LocationEngineProvider.getBestLocationEngine(mContext)

        var request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
            .build()

        locationEngine.requestLocationUpdates(request, callback, Looper.getMainLooper())
        locationEngine.getLastLocation(callback)

    }

    fun initilizieLocationLayer(style: Style){
        val locationComponentOptions = LocationComponentOptions
            .builder(mContext)
            .elevation(5f)
            .accuracyAlpha(.6f)
            .accuracyColor(Color.RED)
            .build()

        val locationComponentActivationOptions = LocationComponentActivationOptions
            .builder(mContext, style)
            .locationComponentOptions(locationComponentOptions)
            .build()


        locationComponent = map.locationComponent

        locationComponent.activateLocationComponent(locationComponentActivationOptions)

        locationComponent.isLocationComponentEnabled = true

        locationComponent.cameraMode = CameraMode.TRACKING

        locationComponent.renderMode = RenderMode.COMPASS



    }

    fun setCameraPosition(location: Location){
        map.animateCamera(CameraUpdateFactory
            .newLatLngZoom(LatLng(location.latitude, location.longitude),13.0))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun drawBysykkelMarkers(){
        val markerOption = MarkerOptions()
        val iconFactory = IconFactory.getInstance(mContext)
        val paint = Paint().also {
            it.color = Color.BLACK
            it.textSize = 20f
            it.style = Paint.Style.FILL
        }

        bysykkelViewModel.stationCompleteList.observe(this, Observer { stations->

            stations.forEach {
                markerOption.title = "bysykkel"
                markerOption.icon = makeMarkerIcon(paint, MarkerType.BYSYKKEL, iconFactory)
                markerOption.snippet = it.id.toString()
                markerOption.position = LatLng(it.stationInfo!!.lat.toDouble(), it.stationInfo.lon.toDouble())

                map.addMarker(markerOption)
            }
        })
    }

    fun makeMarkerIcon(paint: Paint, type: MarkerType, iconFactory: IconFactory): Icon? {
        val drawable = when(type){
            MarkerType.VOI -> resources.getDrawable(R.drawable.ic_voi)
            MarkerType.BYSYKKEL -> resources.getDrawable(R.drawable.ic_bysykkel)
        }

        val zoom = map.cameraPosition.zoom

        val iconSize = when{
            zoom <= 10 -> 5
            zoom > 10 && zoom  <= 12 -> 15
            zoom > 12 && zoom <= 14 -> 45
            zoom > 14 -> 85
            else -> 45
        }

        Log.d("ICONSIZE", iconSize.toString())

        val bitmap = Bitmap.createBitmap(iconSize,iconSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0,0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return iconFactory.fromBitmap(bitmap)
    }

    fun drawVoiBikesMarkers(){
        val markerOption = MarkerOptions()
        val iconFactory = IconFactory.getInstance(mContext)
        val paint = Paint().also {
            it.color = Color.BLACK
            it.textSize = 20f
        }

        voiViewModel.voiBike.observe(this, Observer { bikes->
            bikes.forEach {
                markerOption.title = "voi"
                markerOption.icon = makeMarkerIcon(paint, MarkerType.VOI, iconFactory)
                markerOption.position = LatLng(it.location[0], it.location[1])
                markerOption.snippet = it.id

                map.addMarker(markerOption)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        if(locationEngine != null){
            locationEngine.removeLocationUpdates(callback)
        }
        mapView.onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()

        mapView.let {
            it.onDestroy()
        }
    }

    fun drawAllMarkers(){
        val iconFactory = IconFactory.getInstance(mContext)
        val paint = Paint().also {
            it.color = Color.BLACK
            it.textSize = 20f
            it.style = Paint.Style.FILL
        }


        map.markers.forEach {
            when{
                it.title == "voi" -> it.icon = makeMarkerIcon(paint, MarkerType.VOI, iconFactory)
                else -> it.icon = makeMarkerIcon(paint, MarkerType.BYSYKKEL, iconFactory)
            }
        }
    }

    fun initObservers(){
        map.addOnCameraIdleListener {
            val beforeZoom = zoomLevel
            val afterZoom = map.cameraPosition.zoom

            when{
                beforeZoom > 14 && afterZoom < 14 -> drawAllMarkers()
                beforeZoom > 12 && afterZoom < 12 -> drawAllMarkers()
                beforeZoom > 10 && afterZoom < 10 -> drawAllMarkers()


                afterZoom > 14 && beforeZoom < 14 -> drawAllMarkers()
                afterZoom > 12 && beforeZoom < 12 -> drawAllMarkers()
                afterZoom > 10 && beforeZoom < 10 -> drawAllMarkers()
            }

            zoomLevel = afterZoom
        }
    }
}
