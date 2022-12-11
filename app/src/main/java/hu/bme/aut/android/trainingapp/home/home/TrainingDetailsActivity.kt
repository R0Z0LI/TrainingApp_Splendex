package hu.bme.aut.android.trainingapp.home.home

import android.content.Intent
import android.os.Bundle
import hu.bme.aut.android.trainingapp.BaseActivity
import hu.bme.aut.android.trainingapp.databinding.ActivityTrainingDetailsBinding

class TrainingDetailsActivity : BaseActivity() {


    private lateinit var binding: ActivityTrainingDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle : Bundle? = intent.extras
        val name = bundle?.getString("Name")
        val type = bundle?.getString("Type")
        val date = bundle?.getString("Date")
        val avgPulse = bundle?.getString("AvgPulse")
        val maxPulse = bundle?.getString("MaxPulse")
        val duration = bundle?.getString("Duration")
        val distance = bundle?.getString("Distance")
        val avgSpeed = bundle?.getString("AvgSpeed")
        val maxSpeed = bundle?.getString("MaxSpeed")
        val calories = bundle?.getString("Calories")

        binding.etName.setText(name)
        binding.etType.setText(type)
        binding.tvDate.text = date
        binding.tvAvg.setText(avgSpeed)
        binding.tvMaxSpeed2.setText(maxSpeed)
        binding.tvAvgPulse2.setText(avgPulse)
        binding.tvMaxPulse2.setText(maxPulse)
        binding.tvCalories2.setText(calories)
        binding.tvDistance2.setText(distance)
        binding.tvDuration2.setText(duration)


        binding.fabEdit.setOnClickListener{
            binding.etName.isEnabled = true
            binding.etType.isEnabled = true
            binding.tvAvg.isEnabled = true
            binding.tvMaxSpeed2.isEnabled = true
            binding.tvAvgPulse2.isEnabled = true
            binding.tvMaxPulse2.isEnabled = true
            binding.tvCalories2.isEnabled = true
            binding.tvDistance2.isEnabled = true
            binding.tvDuration2.isEnabled = true
        }

        binding.btnSave.setOnClickListener{
            var data = Intent()
            data.putExtra("Name",binding.etName.text.toString())
            data.putExtra("Type", binding.etType.text.toString())
            data.putExtra("AvgSpeed", binding.tvAvg.text.toString())
            data.putExtra("MaxSpeed", binding.tvMaxSpeed2.text.toString())
            data.putExtra("AvgPulse", binding.tvAvgPulse2.text.toString())
            data.putExtra("MaxPulse", binding.tvMaxPulse2.text.toString())
            data.putExtra("Calories", binding.tvCalories2.text.toString())
            data.putExtra("Distance", binding.tvDistance2.text.toString())
            data.putExtra("Duration", binding.tvDuration2.text.toString())
            data.putExtra("Date", binding.tvDate.text.toString())
            setResult(RESULT_OK,data);
            finish()
        }

    }
}