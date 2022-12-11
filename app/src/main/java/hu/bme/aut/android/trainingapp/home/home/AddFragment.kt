package hu.bme.aut.android.trainingapp.home.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.FragmentAddBinding
import hu.bme.aut.android.trainingapp.home.UserDataHolder
import hu.bme.aut.android.trainingapp.model.Training

class AddFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentAddBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference
    private var userDataHolder: UserDataHolder? = null


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
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")

        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        val homeFragment = HomeFragment()

        binding.btnRegister.setOnClickListener {
            saveData(auth)
            transaction.replace(R.id.flFragment, homeFragment)
            transaction.commit()
        }
    }

    private fun saveData(auth : FirebaseAuth) {

        val uid = auth.currentUser?.uid
        val user = userDataHolder?.getUser()
        val duration = binding.etDuration.text.toString()
        val calories = binding.etCaloriesBurnt.text.toString()
        val distance = binding.etDistance.text.toString()
        val maxSpeed = binding.etMaxSpeed.text.toString()
        val avgSpeed = binding.etAvgSpeed.text.toString()
        val maxPulse = binding.etMaxPulse.text.toString()
        val avgPulse = binding.etAvgPulse.text.toString()
        val name = binding.etName.text.toString()
        val type = binding.etType.text.toString()
        val date = userDataHolder?.getDate()
        val training = Training(name, type, date, duration, calories, distance, avgSpeed, maxSpeed, avgPulse, maxPulse)
        user?.trainings?.add(training)
        var num = user?.trainingDetails?.trainingsDone?.toInt()
        num = num?.plus(1)
        var distanceTraveled = user?.trainingDetails?.distanceTraveled?.toInt()
        distanceTraveled = distanceTraveled?.plus(distance.toInt())
        var caloriesBurnt = user?.trainingDetails?.caloriesBurnt?.toInt()
        caloriesBurnt = caloriesBurnt?.plus(calories.toInt())
        var hoursSpent = user?.trainingDetails?.hoursSpent?.toInt()
        hoursSpent = hoursSpent?.plus(duration.toInt())
        user?.trainingDetails?.trainingsDone = num.toString()
        user?.trainingDetails?.distanceTraveled = distanceTraveled.toString()
        user?.trainingDetails?.caloriesBurnt = caloriesBurnt.toString()
        user?.trainingDetails?.hoursSpent = hoursSpent.toString()
        if(uid != null){
            dataBaseReference.child(uid).setValue(user).addOnCompleteListener{
                if(it.isSuccessful){

                } else {

                }
            }
        }
    }
}