@file:Suppress("DEPRECATION")

package com.ifkusyoba.dicogram.views.detailstory

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ifkusyoba.dicogram.databinding.ActivityDetailStoryBinding
import com.ifkusyoba.dicogram.models.Story

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private val TAG = DetailStoryActivity::class.java.simpleName
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Detail Page"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val data = intent.getParcelableExtra<Story>(EXTRA_DETAIL)
        Glide.with(this)
            .load(data?.photoUrl)
            .apply(RequestOptions().override(80, 80))
            .into(binding.imgDetail)
        binding.apply {
            tvUsername.text = data?.name
            tvDescription.text = data?.description
        }
        val lat = data?.lat
        val lng = data?.lng
        if (lat != null && lng != null) {
            binding.tvLocation.text =
                Geolocator.parseAddressLocation(this, lat.toDouble(), lng.toDouble())
            binding.tvLocation.isVisible = true
        } else {
            binding.tvLocation.isVisible = false
            Log.e(TAG, "Latitude or Longitude is null")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}

object Geolocator {
    fun parseAddressLocation(
        context: Context,
        lat: Double,
        lng: Double
    ): String {
        val geocoder = Geocoder(context)
        val geoLocation =
            geocoder.getFromLocation(lat, lng, 1)
        return if (geoLocation!!.size > 0) {
            val location = geoLocation[0]
            val fullAddress = location.getAddressLine(0)
            StringBuilder("")
                .append(fullAddress).toString()
        } else ({
            Log.e("Geolocator", "Location not found")
        }).toString()
    }
}