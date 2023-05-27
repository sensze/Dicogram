package com.ifkusyoba.dicogram.views.authentication.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifkusyoba.dicogram.databinding.ActivityRegisterBinding
import com.ifkusyoba.dicogram.models.Result
import com.ifkusyoba.dicogram.viewmodel.RegisterViewModel
import com.ifkusyoba.dicogram.viewmodel.ViewModelProvider
import com.ifkusyoba.dicogram.views.authentication.login.LoginActivity
import androidx.lifecycle.ViewModelProvider as ViewModelProviders

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        // *Inisialisasi ViewModel
        val viewModelProvider: ViewModelProvider = ViewModelProvider.getInstance(this)
        registerViewModel = ViewModelProviders(this, viewModelProvider)[RegisterViewModel::class.java]

        button()
        routeToLogin()
    }
    private fun button() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edUsername.text.toString()
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            registerViewModel.register(name, email, password).observe(this) {
                when (it) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Error -> {
                        Toast.makeText(this, "Akun terdaftar!", Toast.LENGTH_LONG).show()
                        showLoading(false)
                    }
                }
            }
        }
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun showLoading(state: Boolean) {
        if (state) {
            binding.loading.root.visibility = View.VISIBLE
        } else {
            binding.loading.root.visibility = View.GONE
        }
    }
    private fun routeToLogin() {
        binding.btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}