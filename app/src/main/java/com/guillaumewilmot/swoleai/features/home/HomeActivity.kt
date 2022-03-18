package com.guillaumewilmot.swoleai.features.home

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.databinding.ActivityHomeBinding
import com.guillaumewilmot.swoleai.features.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ParentActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        if (false) {
            binding = ActivityHomeBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } else {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        startActivity(Intent(this, OnboardingActivity::class.java))
    }
}