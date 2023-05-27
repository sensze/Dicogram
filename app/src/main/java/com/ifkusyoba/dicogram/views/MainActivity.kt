package com.ifkusyoba.dicogram.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.ifkusyoba.dicogram.R
import com.ifkusyoba.dicogram.databinding.ActivityMainBinding
import com.ifkusyoba.dicogram.services.paging.PagingAdapter
import com.ifkusyoba.dicogram.viewmodel.LoginViewModel
import com.ifkusyoba.dicogram.viewmodel.StoryViewModel
import com.ifkusyoba.dicogram.viewmodel.ViewModelProvider as ViewModelProviders
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifkusyoba.dicogram.services.paging.PagingLoadingAdapter
import com.ifkusyoba.dicogram.views.authentication.login.LoginActivity
import com.ifkusyoba.dicogram.views.maps.MapsActivity
import com.ifkusyoba.dicogram.views.uploadstory.UploadStoryActivity
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagingAdapter: PagingAdapter
    private lateinit var viewModelProvider: ViewModelProviders
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var listStoryViewModel: StoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvStoryItem.layoutManager = LinearLayoutManager(this)
        binding.rvStoryItem.setHasFixedSize(true)

        // *Inisialisasi viewmodel
        viewModelProvider = ViewModelProviders.getInstance(this)
        listStoryViewModel = ViewModelProvider(this, viewModelProvider)[StoryViewModel::class.java]
        loginViewModel = ViewModelProvider(this, viewModelProvider)[LoginViewModel::class.java]
        pagingAdapter = PagingAdapter()

        listStoryViewModel.getUser().observe(this) {it ->
            if (it.isLogin) {
                loadStory()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // *Setup action button
        binding.fab.setOnClickListener {
            val intent = Intent(this, UploadStoryActivity::class.java)
            startActivity(intent)
        }
        binding.refresh.setOnRefreshListener {
            refreshStory()
        }
    }

    private fun loadStory() {
        binding.rvStoryItem.adapter = pagingAdapter.withLoadStateFooter(
            footer = PagingLoadingAdapter {
                pagingAdapter.retry()
            }
        )
        listStoryViewModel.getStory().observe(this) {
            pagingAdapter.submitData(lifecycle, it)
            showLoading(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_main_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                refreshStory()
            }
            R.id.maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.logout -> {
                loginViewModel.logout()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshStory() {
        binding.refresh.isRefreshing = true
        pagingAdapter.refresh()
        Timer().schedule(2000) {
            binding.refresh.isRefreshing = false
            binding.rvStoryItem.smoothScrollToPosition(0)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.loading.root.visibility = View.VISIBLE
        } else {
            binding.loading.root.visibility = View.GONE
        }
    }

    // Set Location
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getLocation()
            }
        }

    private fun getLocation() {
        if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener {location ->
                if(location != null) {
                    lat = location.latitude.toDouble()
                    lng = location.longitude.toDouble()

                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Location")
                    alertDialog.setMessage("Location saved: $lat, $lng")
                    alertDialog.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alertDialog.show()
                } else {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Location")
                    alertDialog.setMessage("Location not found")
                    alertDialog.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alertDialog.show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}