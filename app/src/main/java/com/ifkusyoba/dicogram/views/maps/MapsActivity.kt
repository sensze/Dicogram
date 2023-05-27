package com.ifkusyoba.dicogram.views.maps

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.ifkusyoba.dicogram.R
import com.ifkusyoba.dicogram.databinding.ActivityMapsBinding
import com.ifkusyoba.dicogram.viewmodel.MapsViewModel
import com.ifkusyoba.dicogram.models.Result as Results
import com.ifkusyoba.dicogram.viewmodel.ViewModelProvider as ViewModelProviders

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mapsViewModel: MapsViewModel
    private var boundBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Maps"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // *Inisialisasi viewmodel
        val viewModelProvider = ViewModelProviders.getInstance(this)
        mapsViewModel = ViewModelProvider(
            this,
            viewModelProvider
        )[MapsViewModel::class.java]
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        getStory()
        setMapStyle()
    }

    // *Fungsi getstory
    private fun getStory() {
        mapsViewModel.getUser().observe(this) {
            val token = "Bearer " + it.token
            mapsViewModel.getLocation(token).observe(this) { stories ->
                when (stories) {
                    is Results.Success -> {
                        stories.data.listStory.forEach() { story ->
                            val latLng = LatLng(story.lat!!, story.lng!!)
                            val marker = mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(story.name)
                                    .snippet(story.description)
                                    .icon(BitmapDescriptorFactory.defaultMarker())
                            )
                            marker?.tag = story
                            boundBuilder.include(marker?.position ?: latLng)
                        }
                    }
                    is Results.Error -> {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setTitle("Error")
                        alertDialog.setMessage("Error: ${stories.error}")
                        alertDialog.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        alertDialog.show()
                    }
                    is Results.Loading -> {
                        // *Nothing
                    }
                    else -> {
                        // *Nothing
                    }
                }
            }
        }
    }

    // *Location
    private fun location() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // *Setup permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                location()
            } else {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Error")
                alertDialog.setMessage("Permission denied")
                alertDialog.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }
    private fun setMapStyle(){
        mMap.setMapStyle(
            com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle(
                this,
                R.raw.map_style
            )
        )
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}