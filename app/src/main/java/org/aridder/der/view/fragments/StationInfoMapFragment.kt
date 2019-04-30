package org.aridder.der.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_station_info_map.*
import org.aridder.der.R

class StationInfoMapFragment : SharedFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stationInfoViewModel.stationComplete.observe(this, Observer {
            if(it != null){
                textView_fragment_station_info_number_of_bikes.text = it.stationStatus!!.num_bikes_available.toString()
                textView_fragment_station_info_number_of_locks.text = it.stationStatus.num_docks_available.toString()
                textView_fragment_station_info_station_id.text = "# ${it.id}"
                textView_fragment_station_info_station_name.text = it.stationInfo!!.name
                textView_fragment_station_info_station_address.text = it.stationInfo.address

                button_fragment_station_info_go_to_details_page.setOnClickListener {
                    switchFromMapFragmentToStationDetailsFragment()
                }

                button_fragment_station_info_go_to_bysykkel_app.setOnClickListener {
                    switchToBysykkelApp(stationInfoViewModel.stationComplete.value!!.id)
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_station_info_map, container, false)
    }

}
