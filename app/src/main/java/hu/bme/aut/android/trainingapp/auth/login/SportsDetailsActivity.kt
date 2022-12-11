package hu.bme.aut.android.trainingapp.auth.login

import android.os.Bundle
import hu.bme.aut.android.trainingapp.BaseActivity
import hu.bme.aut.android.trainingapp.databinding.ActivitySportsDetailsBinding

class SportsDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivitySportsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}