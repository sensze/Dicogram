package com.ifkusyoba.dicogram.views.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ifkusyoba.dicogram.databinding.ActivitySplashScreenBinding
import com.ifkusyoba.dicogram.services.SharedPreferences
import com.ifkusyoba.dicogram.utility.dataStore
import com.ifkusyoba.dicogram.views.MainActivity
import com.ifkusyoba.dicogram.views.authentication.register.RegisterActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        hideAppbar()
        loadSplash()

    }

    private fun loadSplash(){
        val sharedPreferences = SharedPreferences.getInstance(application.dataStore)
        Handler(Looper.getMainLooper()).postDelayed({
            sharedPreferences.getData().onEach { user ->
                if (user.token.isEmpty()) {
                    startActivity(Intent(this, RegisterActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finishAffinity()
            }.launchIn(lifecycleScope)
        }, 5000L)
    }

    private fun hideAppbar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}