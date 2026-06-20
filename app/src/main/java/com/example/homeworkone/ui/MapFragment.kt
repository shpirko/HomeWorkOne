package com.example.homeworkone.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.homeworkone.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    private var googleMap: GoogleMap? = null
    private var pendingLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_map, container, false)
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { map ->
            googleMap = map
            // Zoom to pending location if one was requested while loading
            pendingLocation?.let {
                zoom(it.latitude, it.longitude)
            }
        }
        
        return v
    }

    fun zoom(lat: Double, lon: Double) {
        if (lat != 0.0 || lon != 0.0) {
            val location = LatLng(lat, lon)
            if (googleMap == null) {
                pendingLocation = location
                return
            }
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(location).title("Score Location"))
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }
}