package com.ifkusyoba.dicogram.views.uploadstory

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.ifkusyoba.dicogram.databinding.ActivityCameraBinding
import com.ifkusyoba.dicogram.utility.Files

class CameraActivity : AppCompatActivity() {
    private val TAG = CameraActivity::class.java.simpleName
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        button()
    }

    private fun button() {
        binding.apply {
            btnShutter.setOnClickListener {
                shutter()
            }
            btnSwitch.setOnClickListener {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                    else CameraSelector.DEFAULT_BACK_CAMERA
                initCamera()
            }
        }
    }
    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        initCamera()
    }

    // *shutter
    private fun shutter(){
        val imageCapture = imageCapture?:return
        val image = Files.createFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(image).build()
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val intent = Intent()
                intent.putExtra("picture", image)
                intent.putExtra("isBackCamera", cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                setResult(UploadStoryActivity.CAMERA_X_RESULT, intent)
                finish()
            }

            override fun onError(exception: ImageCaptureException) {
                val alertDialog = AlertDialog.Builder(this@CameraActivity)
                alertDialog.setTitle("Error")
                alertDialog.setMessage("Use case binding failed: ${exception.message}")
                alertDialog.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }

        })
    }
    // *Init camera
    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (error: Exception) {
                Log.e(TAG, "Use case binding failed", error)
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Error")
                alertDialog.setMessage("Use case binding failed: ${error.message}")
                alertDialog.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}