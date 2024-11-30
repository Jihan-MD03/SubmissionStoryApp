package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }
}
