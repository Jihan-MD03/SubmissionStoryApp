package com.dicoding.picodiploma.loginwithanimation.view.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class SplashActivity : AppCompatActivity() {


    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[MainViewModel::class.java]


        val appLogo = findViewById<ImageView>(R.id.logoImageView)
        val appName = findViewById<TextView>(R.id.appNameText)

        // Animasi Scale-Up untuk Logo
        ObjectAnimator.ofFloat(appLogo, "scaleX", 0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(appLogo, "scaleY", 0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        // Animasi Fade-In untuk Nama Aplikasi
        ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f).apply {
            duration = 1000
            startDelay = 500
            start()
        }

        appName.postDelayed({
            // Mengecek session pengguna
            viewModel.getSession().observe(this) { user ->
                if (user.isLogin) {
                    // Jika sudah login, arahkan ke StoryActivity
                    startActivity(Intent(this, StoryActivity::class.java))
                } else {
                    // Jika belum login, arahkan ke WelcomeActivity
                    startActivity(Intent(this, WelcomeActivity::class.java))
                }
                finish()  // Tutup SplashActivity setelah pengalihan
            }
        }, 1500)
    }
}
