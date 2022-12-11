package hu.bme.aut.android.trainingapp.home.friends

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.trainingapp.databinding.ActivityFriendDetailsBinding

class FriendDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFriendDetailsBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle : Bundle? = intent.extras
        val first = bundle?.getString("First")
        val second = bundle?.getString("Second")
        val user = bundle?.getString("User")
        val calories = bundle?.getString("CaloriesBurnt")
        val distance = bundle?.getString("DistanceTraveled")
        val hours = bundle?.getString("HoursSpent")
        val training = bundle?.getString("TrainingsDone")

        initComponents(first, second, user, calories, distance, hours, training)
    }

    private fun initComponents(
        first: String?,
        second: String?,
        user: String?,
        calories: String?,
        distance: String?,
        hours: String?,
        training: String?
    ) {
        binding.tvFirstName.text = first
        binding.tvSecondName.text = second
        binding.tvUserName.text = user
        binding.tvCaloriesBurnt.text = calories
        binding.tvDistanceTraveled.text = distance
        binding.tvHoursSpent.text = hours
        binding.tvTrainingsDone.text = training
    }
}