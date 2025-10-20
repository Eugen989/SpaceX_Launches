package com.example.spacexlaunches.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spacexlaunches.R
import com.example.spacexlaunches.data.Api
import com.example.spacexlaunches.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = Api();
        api.getData();

        binding = ActivityMainBinding.inflate(layoutInflater);

        setContentView(binding.root);
    }
}