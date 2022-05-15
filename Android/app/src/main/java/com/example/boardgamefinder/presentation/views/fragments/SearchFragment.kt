package com.example.boardgamefinder.presentation.views.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.boardgamefinder.R
import com.example.boardgamefinder.core.EventClusteringItem
import com.example.boardgamefinder.core.MyIconRendered
import com.example.boardgamefinder.databinding.FragmentSearchBinding
import com.example.boardgamefinder.domain.models.Event
import com.example.boardgamefinder.presentation.viewModels.SearchViewModel
import com.example.boardgamefinder.presentation.views.activities.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import java.text.SimpleDateFormat

/**
 * Fragment for search (map and list) tab
 */
internal class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var clusterManager: ClusterManager<EventClusteringItem>

    private var eventBottomSheetLayout: ConstraintLayout? = null
    private var eventBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    internal companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 90
        private const val GPS_REQUEST_CODE = 2
    }

    //ToDo onviewcreated
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geocoder = Geocoder(context)

        initializeMap()

        eventBottomSheetLayout = binding.root.findViewById(R.id.event_bottom_sheet)
        eventBottomSheetBehavior = BottomSheetBehavior.from(eventBottomSheetLayout!!)
        eventBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        return binding.root
    }

    private fun initializeMap(){
        // initialize map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        // async map
        mapFragment!!.getMapAsync { googleMap ->
            map = googleMap

            // clusters
            clusterManager = ClusterManager<EventClusteringItem>(activity, map)

            // min cluster size
            val clusterRenderer = MyIconRendered(requireActivity(), map, clusterManager)
            clusterRenderer.minClusterSize = 2

            // set renderer
            clusterManager.renderer = clusterRenderer

            // Point the map's listeners at the listeners implemented by the cluster manager.
            map.setOnCameraIdleListener(clusterManager)
            map.setOnMarkerClickListener(clusterManager)

            clusterManager.setOnClusterItemClickListener {
                openBottomSheet(it.getId())
                true
            }

            clusterManager.setOnClusterClickListener {zoomInCluster(it)}

            map.isIndoorEnabled = true

            // remove all POIs
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style_json
                )
            )

            map.setOnMyLocationButtonClickListener{ enableGPS() }

            setupPermissions()

            searchViewModel.events.observe(viewLifecycleOwner){updateMap()}
        }

        searchViewModel.getEvents()
    }

    private fun zoomInCluster(it: Cluster<EventClusteringItem>) : Boolean{
        // zoom in cluster
        val builder = LatLngBounds.Builder()
        for (marker in it.items) builder.include(marker.position)
        val bounds = builder.build()
        // offset from edges of the map in pixels
        val padding = 100
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        map.animateCamera(cu)
        return true
    }

    // return false if it is already enabled
    private fun enableGPS() : Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return false

        // ToDo to string resources
        AlertDialog.Builder(context)
            .setTitle("GPS")
            .setMessage("To continue, turn on device location")
            .setPositiveButton(
                "Ok"
            ) { _: DialogInterface?, _: Int ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                //ToDo
                startActivityForResult(intent, GPS_REQUEST_CODE)
            }
            .setNegativeButton(
                "No thanks"
            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setCancelable(true)
            .show()

        return true
    }

    // after enableGPS done
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (requestCode == GPS_REQUEST_CODE && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            // ToDo to string resources
            Toast.makeText(context, "GPS not enabled", Toast.LENGTH_SHORT).show()
    }

    private fun setupPermissions() {
        val permission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
            // override in parent activity onRequestPermissionsResult
        else {
            locationGranted()
        }
    }

    fun locationGranted(){
        enableUserLocation()
        zoomToUserLocation()
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return
        map.isMyLocationEnabled = true
    }

    private fun zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val locationTask = fusedLocationProviderClient.lastLocation
        locationTask.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    private fun updateMap(){
        clusterManager.clearItems()

        searchViewModel.events.value?.let {
            for(i in it.indices){
                val marker = EventClusteringItem(
                    it[i].latitude,
                    it[i].longitude,
                    it[i].title,
                    it[i].location,
                    EventClusteringItem.MarkerType.EVENT,
                    resources.getDrawable(R.drawable.ic_marker_dice),
                    i
                )

                clusterManager.addItem(marker)
                //val c =  clusterManager.algorithm.items.size
            }
        }

        clusterManager.cluster()
    }

    private fun openBottomSheet(id: Int){
        eventBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        searchViewModel.events.value?.let {
            setupBottomSheet(it[id])
            eventBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setupBottomSheet(event: Event){
        if(eventBottomSheetLayout == null)
            return

        val title: TextView = eventBottomSheetLayout!!.findViewById(R.id.title)
        val location: TextView = eventBottomSheetLayout!!.findViewById(R.id.location)
        val date: TextView = eventBottomSheetLayout!!.findViewById(R.id.date)
        val image: ImageView = eventBottomSheetLayout!!.findViewById(R.id.image)
        val openButton: Button = eventBottomSheetLayout!!.findViewById(R.id.openButton)

        title.text = event.title
        location.text = event.location

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        date.text = dateFormatter.format(event.eventDate.time)

        // set image
        Glide.with(this)
            .load(event.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.color.dark_gray)
            .transform(CenterCrop(), GranularRoundedCorners(50F, 50F, 0F, 0F))
            .into(image)

        // open event page
        openButton.setOnClickListener {
            eventBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            (activity as MainActivity).replaceFragment(EventDetailsFragment(event.id))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}