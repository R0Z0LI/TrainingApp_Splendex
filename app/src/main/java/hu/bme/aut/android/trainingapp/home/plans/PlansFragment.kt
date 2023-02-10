package hu.bme.aut.android.trainingapp.home.plans

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.FragmentCalendarBinding
import hu.bme.aut.android.trainingapp.home.UserDataHolder
import hu.bme.aut.android.trainingapp.model.Exercise
import hu.bme.aut.android.trainingapp.model.Training
import hu.bme.aut.android.trainingapp.model.TrainingDays
import hu.bme.aut.android.trainingapp.model.TrainingPlan

class PlansFragment : Fragment(R.layout.fragment_calendar){
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var database: DatabaseReference
    private var userDataHolder: UserDataHolder? = null
    private lateinit var name: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDataHolder = if(activity is UserDataHolder){
            activity as UserDataHolder?
        } else{
            throw RuntimeException(
                "Activity must implement CurrencyDataHolder interface!"
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("TrainingPlans")
        readData()
        binding.btnTraining.setOnClickListener{
            val intent = Intent(requireContext(), TrainingActivity::class.java)
            intent.putExtra("Name", name)
            startActivity(intent)
        }
    }

    private fun readData(){
        database.child("TrainingPlanOne").get().addOnSuccessListener {
            if (it.exists()) {
                name = it.child("name").value.toString()
            }
        }
    }


    private fun saveData() {

        val trainingDays = arrayListOf<TrainingDays>()
        val training = Training("Beginning", "Full Body", userDataHolder?.getDate(), "9", "200", "0", "0", "0", "0", "0")
        val exercise1 = Exercise("Jumping Jacks", "20 sec", "Start with your feet together and your arms by your sides, then jump up with your feet apart and your hands overhead.")
        val exercise2 = Exercise("Incline push-ups", "10x", "Start in the regular push-up position but with your hands elevated on a chair or bench.")
        val exercise3 = Exercise("Knee push-ups", "6x", "Start in the regular push-up position, then let your knee touch the floor and raise your feet up off the floor")
        val exercises = arrayListOf<Exercise>()
        exercises.add(exercise1)
        exercises.add(exercise2)
        exercises.add(exercise3)
        val trainingDay1 = TrainingDays("Day 1", "Full body", "9 mins", training, exercises)
        val trainingDay2 = TrainingDays("Day 1", "Full body", "9 mins", training, exercises)
        trainingDays.add(trainingDay1)
        trainingDays.add(trainingDay2)
        val trainingPlan = TrainingPlan("Beginner Full Body", trainingDays)
        database.child("TrainingPlanOne").setValue(trainingPlan).addOnCompleteListener{
            if(it.isSuccessful){
            } else {
                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }

    }
}