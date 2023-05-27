package com.ifkusyoba.dicogram.views.authentication.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifkusyoba.dicogram.databinding.ActivityLoginBinding
import com.ifkusyoba.dicogram.models.Result
import com.ifkusyoba.dicogram.models.Users
import com.ifkusyoba.dicogram.viewmodel.LoginViewModel
import com.ifkusyoba.dicogram.viewmodel.ViewModelProvider
import com.ifkusyoba.dicogram.views.MainActivity
import com.ifkusyoba.dicogram.views.authentication.register.RegisterActivity
import androidx.lifecycle.ViewModelProvider as ViewModelProviders

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        // *Inisialisasi ViewModel
        val viewModelProvider: ViewModelProvider = ViewModelProvider.getInstance(this)
        loginViewModel = ViewModelProviders(
            this,
            viewModelProvider
        )[LoginViewModel::class.java]
        button()
        routeToRegister()
    }

    private fun button() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            loginViewModel.login(email, password).observe(this) {
                when (it) {
                    is Result.Success -> {
                        showLoading(false)
                        val response = it.data
                        saveData(
                            Users(
                                response.loginResult.name.toString(),
                                response.loginResult.userId.toString(),
                                response.loginResult.token.toString(),
                                true
                            )
                        )
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    is Result.Loading -> showLoading(true)
                    is Result.Error -> {
                        Toast.makeText(this, "Login Gagal!", Toast.LENGTH_LONG).show()
                        showLoading(false)
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

    private fun routeToRegister() {
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveData(user: Users) {
        loginViewModel.saveUser(user)
    }
}

