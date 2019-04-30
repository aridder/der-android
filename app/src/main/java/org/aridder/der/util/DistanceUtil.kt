package org.aridder.der.util

import android.location.Location
import org.aridder.der.model.StationInfo

fun getDistanceToStationFromUserLocation(userPosition: Location?, station: StationInfo?): Float {
    val position = userPosition?: return 0f
    station ?: return 0f

    val stationPosition = Location("")
    stationPosition.latitude = station.lat.toDouble()
    stationPosition.longitude = station.lon.toDouble()

    return position.distanceTo(stationPosition)
}