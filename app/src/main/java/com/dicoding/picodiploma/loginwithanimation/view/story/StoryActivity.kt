package com.dicoding.picodiploma.loginwithanimation.view.story

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.StoryViewModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class StoryActivity : AppCompatActivity() {

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        // Dapatkan ViewModel
        val userRepository = Injection.provideUserRepository(applicationContext)
        val factory = ViewModelFactory(userRepository)
        storyViewModel = ViewModelProvider(this, factory).get(StoryViewModel::class.java)

        // Inisialisasi RecyclerView
        storyAdapter = StoryAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = storyAdapter

        // Ambil token dan fetch data
        val token = "your_token_here" // Dapatkan token yang benar
        storyViewModel.getStories(token)

        // Observasi LiveData dari ViewModel
        storyViewModel.stories.observe(this, Observer { stories ->
            storyAdapter.submitList(stories) // Kirim data ke adapter
        })

        storyViewModel.error.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show() // Tampilkan pesan error jika ada
        })
    }
}
