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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_map, container, false)
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { map ->
            googleMap = map
        }
        
        return v
    }

    fun zoom(lat: Double, lon: Double) {
        var finalLat = lat
        var finalLon = lon

        // Fallback for old records without locations
        if (lat == 0.0 && lon == 0.0) {
            val disneyLocations = listOf(
                Pair(28.4177, -81.5812), // Magic Kingdom
                Pair(28.3747, -81.5494), // Epcot
                Pair(28.3575, -81.5583), // Hollywood Studios
                Pair(28.3529, -81.5907)  // Animal Kingdom
            )
            val fallback = disneyLocations.random()
            finalLat = fallback.first
            finalLon = fallback.second
        }

        val location = LatLng(finalLat, finalLon)
        googleMap?.clear()
        googleMap?.addMarker(MarkerOptions().position(location).title("Score Location"))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}