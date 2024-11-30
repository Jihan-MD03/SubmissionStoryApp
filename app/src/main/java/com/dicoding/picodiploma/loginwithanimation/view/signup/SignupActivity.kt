package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.AuthViewModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel
        authViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[AuthViewModel::class.java]

        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun setupView() {
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

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()  // Mengambil nilai nama dari EditText
            val email = binding.emailEditText.text.toString()
            if (email.isEmpty() || email.length < 8) {
                binding.passwordEditText.error = "Format Email tidak sesuai"
                return@setOnClickListener
            }

            val password = binding.passwordEditText.text.toString()

            if (password.isEmpty() || password.length < 8) {
                binding.passwordEditText.error = "Password harus minimal 8 karakter"
                return@setOnClickListener
            }

            showLoading(true)

            // Panggil API register melalui AuthViewModel
            authViewModel.register(name, email, password) { success, message ->
                showLoading(false)
                if (success) {
                    Toast.makeText(this, "Register berhasil: $message", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Register gagal: $message", Toast.LENGTH_SHORT).show()
                }


                if (success && !isFinishing && !isDestroyed) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan bagikan pengalaman di dicoding.")
                        setPositiveButton("Lanjut") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signupButton.isEnabled = !isLoading // Nonaktifkan tombol saat loading
    }
}