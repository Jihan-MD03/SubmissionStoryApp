package com.dicoding.picodiploma.loginwithanimation.view.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.AuthViewModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        // Panggil getUserSession untuk mengambil data yang disimpan dan set di EditText
        authViewModel.getUserSession { userSession ->
            userSession?.let {
                // Jika session ada, set data yang sudah tersimpan di EditText
                binding.emailEditText.setText(userSession.email)
                binding.passwordEditText.setText(userSession.password) // Menampilkan password yang disimpan
            }
        }
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
        binding.loginButton.setOnClickListener {
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
            // Panggil API login melalui AuthViewModel
            authViewModel.login(email, password) { success, message ->
                if (success) {
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()

                    // Ambil token dari response login (dari UserPreference atau session)
                    val token =
                        "token_from_response"  // Gantilah dengan token yang valid yang didapat dari response

                    // Simpan token ke DataStore
                    authViewModel.saveToken(token)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login gagal: $message", Toast.LENGTH_SHORT).show()
                }
            }
            if (!isFinishing && !isDestroyed) {
                AlertDialog.Builder(this).apply {
                    setTitle("Yeah!")
                    setMessage("Anda berhasil login. Sudah tidak sabar untuk berbagi pengalaman nih!")
                    setPositiveButton("Lanjut") { _, _ ->
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            }

        }
    }
}