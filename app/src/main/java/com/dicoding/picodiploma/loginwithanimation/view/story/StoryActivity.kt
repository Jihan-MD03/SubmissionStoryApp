package com.dicoding.picodiploma.loginwithanimation.view.story

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.StoryViewModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        // Dapatkan ViewModel
        val userRepository = Injection.provideUserRepository(applicationContext) // Pastikan ada fungsi ini di Injection
        val storyRepository = Injection.provideStoryRepository(applicationContext) // Pastikan fungsi ini ada
        val factory = ViewModelFactory(userRepository, storyRepository)
        storyViewModel = ViewModelProvider(this, factory).get(StoryViewModel::class.java)

        // Inisialisasi RecyclerView
        storyAdapter = StoryAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = storyAdapter

        // Ambil token dengan cara yang benar menggunakan coroutine
        lifecycleScope.launch {
            val token = getTokenFromPreference()  // Ambil token dari preferensi secara asinkron
            Log.d("Token", "Retrieved token: $token")

            // Mengambil data stories dengan token
            storyViewModel.getStories(token)

            // Observasi LiveData stories dan error
            storyViewModel.stories.observe(this@StoryActivity) { stories ->
                Log.d("StoryActivity", "Stories loaded: ${stories.size}")
                if (stories.isNotEmpty()) {
                    storyAdapter.submitList(stories)  // Kirim data ke adapter
                } else {
                    Toast.makeText(this@StoryActivity, "No stories found", Toast.LENGTH_SHORT).show()  // Jika tidak ada cerita
                }
            }

            storyViewModel.error.observe(this@StoryActivity) { error ->
                Toast.makeText(this@StoryActivity, error, Toast.LENGTH_SHORT).show()  // Tampilkan error jika ada
                Log.e("StoryActivity", "Error: $error")
            }
        }
    }

    // Fungsi untuk mengambil token dari preferensi atau session
    private suspend fun getTokenFromPreference(): String {
        // Ambil token dari UserPreference atau session
        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
        return userPreference.getToken() ?: ""  // Mengembalikan token atau string kosong jika token tidak ada
    }
}



