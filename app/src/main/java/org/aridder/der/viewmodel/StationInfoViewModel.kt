package org.aridder.der.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.aridder.der.model.*

class StationInfoViewModel(application: Application): AndroidViewModel(application){
    val voi = MutableLiveData<Voi>()
    val stationComplete = MutableLiveData<StationComplete>()
    val position = MutableLiveData<Location>()
}