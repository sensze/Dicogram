package com.ifkusyoba.dicogram.views.uploadstory

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ifkusyoba.dicogram.R
import com.ifkusyoba.dicogram.databinding.ActivityUploadStoryBinding
import com.ifkusyoba.dicogram.utility.Files
import com.ifkusyoba.dicogram.viewmodel.UploadStoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.ifkusyoba.dicogram.models.Result as Results
import com.ifkusyoba.dicogram.viewmodel.ViewModelProvider as ViewModelProviders

class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private val TAG = UploadStoryActivity::class.java.simpleName
    private lateinit var uploadStoryViewModel: UploadStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lng: Double? = null
    private var getFile: File? = null
    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Upload Story"

        // *Inisialisasi viewmodel
        val viewModelProvider = ViewModelProviders.getInstance(binding.root.context)
        uploadStoryViewModel = ViewModelProvider(
            this,
            viewModelProvider
        )[UploadStoryViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        button()
    }

    private fun button() {
        binding.apply {
            btnGallery.setOnClickListener {
                initGallery()
            }
            btnCamera.setOnClickListener {
                if (!allPermissionGranted()) {
                    ActivityCompat.requestPermissions(
                        this@UploadStoryActivity,
                        REQUIRED_PERMISSIONS,
                        REQUEST_CODE_PERMISSIONS
                    )
                } else {
                    initCamera()
                }
            }
            btnUpload.setOnClickListener {
                upload()
            }
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    location()
                }
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    // *Set Camera Permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) {
                initCamera()
            } else {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Permission")
                alertDialog.setMessage("Permission is required to use this feature")
                alertDialog.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }
    }

    // *Set Location Permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {isGranted ->
            if (isGranted) {
                location()
            } else {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Permission")
                alertDialog.setMessage("Permission is required to use this feature")
                alertDialog.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }
    @SuppressLint("MissingPermission")
    private fun location() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lng = location.longitude
                    val currentLocation =
                        Geolocator.parseAddressLocation(this@UploadStoryActivity, lat!!, lng!!)
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Location")
                    alertDialog.setMessage("Location saved: $currentLocation")
                    alertDialog.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alertDialog.show()
                } else {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Permission")
                    alertDialog.setMessage("Permission is required to use this feature")
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

    // *Intent Gallery
    private fun initGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_image))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val image: Uri = it.data?.data as Uri
                val file = Files.uriToFile(image, this)
                getFile = file
                binding.ivPreview.setImageURI(image)
            }
        }

    // *Intent Camera
    private fun initCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile
            image = Files.rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )
            binding.ivPreview.setImageBitmap(image)
        }
    }

    // *Upload
    private fun upload() {
        uploadStoryViewModel.getUser().observe(this) {
            val token = "Bearer ${it.token}"
            if (getFile != null) {
                val file = Files.reduceFileImage(getFile as File, true)
                val caption = "${binding.edCaption.text}".toRequestBody("text/plain".toMediaType())
                val imageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part =
                    MultipartBody.Part.createFormData("photo", file.name, imageFile)
                uploadStoryViewModel.getUser().observe(this) {
                    uploadStoryViewModel.upload(token, imageMultipart, caption, lat, lng)
                        .observe(this) { upload ->
                            when (upload) {
                                is Results.Success -> {
                                    showLoading(false)
                                    val alertDialog = AlertDialog.Builder(this)
                                    alertDialog.setTitle("Upload")
                                    alertDialog.setMessage("Upload Success")
                                    alertDialog.setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                        finish()
                                    }
                                    alertDialog.show()
                                }
                                is Results.Error -> {
                                    showLoading(false)
                                    val alertDialog = AlertDialog.Builder(this)
                                    alertDialog.setTitle("Upload")
                                    alertDialog.setMessage("Upload Failed")
                                    alertDialog.setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    alertDialog.show()
                                }
                                is Results.Loading -> {
                                    showLoading(true)
                                }
                            }
                        }
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.loading.root.visibility = View.VISIBLE
        } else {
            binding.loading.root.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
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