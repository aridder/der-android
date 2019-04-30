package org.aridder.der.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.minimize.android.rxrecycleradapter.RxDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_station_list.*
import org.aridder.der.R
import org.aridder.der.model.StationComplete
import org.aridder.der.util.getDistanceToStationFromUserLocation
import org.aridder.der.viewmodel.BysykkelViewModel
import kotlin.math.roundToInt

class StationListFragment : SharedFragment() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var bysykkelViewModel: BysykkelViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bysykkelViewModel = ViewModelProviders.of(this).get(BysykkelViewModel::class.java)


        //Create the adapter and sets the datasource to the recyclerView item. Here is the dataset empty, but we
        //fill while listening to the LiveData from the viewModel.
        val stationAvailabilityAdapter =
            RxDataSource<org.aridder.der.databinding.ItemStationBinding, StationComplete>(
                R.layout.item_station,
                listOf()
            )

        //binds the adapter to recyclerView
        stationAvailabilityAdapter.bindRecyclerView(view.findViewById(R.id.recycler_stations_list))

        recycler_stations_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context!!)
        }


        stationAvailabilityAdapter
            .asObservable()
            .subscribe {
                val binding = it.viewDataBinding ?: return@subscribe
                binding.textViewItemStationAvailabilityId.text = "# ${it.item?.stationInfo?.station_id}"
                binding.textViewItemStationAvailabilityTitle.text = it.item?.stationInfo?.name
                binding.textViewItemStationAvailabilitySubtitle.text = it.item?.stationInfo?.address
                binding.textViewItemStationAvailabilityLocks.text = it.item?.stationStatus?.num_docks_available.toString()
                binding.textViewItemStationAvailabilityDistance.text = "${getDistanceToStationFromUserLocation(stationInfoViewModel.position.value, it.item?.stationInfo).roundToInt().toString()} meter"
                binding.textViewItemStationAvailabilityBikes.text = it.item?.stationStatus?.num_bikes_available.toString()
                binding.buttonItemStationAvailabilityOpenStationInBysykkelApp.setOnClickListener { view ->
                    switchToBysykkelAll(it.item?.stationInfo?.station_id)
                }
                binding.buttonItemStationAvailabilityGoToStationDetails.setOnClickListener { view ->
                    switchToBysykkelDetailsFragment(it.item?.stationInfo?.station_id)
                }
                binding.imageViewLockDistanceItemStationAvailability.setImageResource(R.drawable.ic_directions_walk_black_24dp)
                binding.imageViewBikeIconItemStationAvailability.setImageResource(R.drawable.ic_directions_bike_black_24dp)
                binding.imageViewLockIconItemStationAvailability.setImageResource(R.drawable.ic_local_parking_black_24dp)

            }.addTo(compositeDisposable)

        bysykkelViewModel.stationCompleteList.observe(this, Observer {
            stationAvailabilityAdapter.updateDataSet(it)
            stationAvailabilityAdapter.updateAdapter()
        })

        bysykkelViewModel.stationCompleteLoading.observe(this, Observer {
            if (it) {
                //show loading
            } else {
                //show list
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_station_list, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.clear()
    }

    private fun switchToBysykkelDetailsFragment(id: String?) {
        stationInfoViewModel.stationComplete.value = bysykkelViewModel.stationCompleteList.value!!.firstOrNull {
            it.stationInfo?.station_id == id
        }

        switchFromStationListToStationDetails()
    }

    private fun switchToBysykkelAll(id: String?) {
        switchToBysykkelApp(id!!)
    }


}
