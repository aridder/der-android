package org.aridder.der.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_station_info_map.textView_fragment_station_info_number_of_bikes
import kotlinx.android.synthetic.main.fragment_station_info_map.textView_fragment_station_info_station_name
import kotlinx.android.synthetic.main.fragment_voi_info_map.*
import org.aridder.der.R

class VoiInfoMapFragment: SharedFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stationInfoViewModel.voi.observe(this, Observer {
            if(it != null){
                textView_fragment_voi_name.text = it.name
                textView_fragment_voi_info_electricity.text = "${it.battery.toString()}%"
                button_fragment_voi_scan_qr.setOnClickListener {
                    switchToVoiApp()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voi_info_map, container, false)
    }
}
