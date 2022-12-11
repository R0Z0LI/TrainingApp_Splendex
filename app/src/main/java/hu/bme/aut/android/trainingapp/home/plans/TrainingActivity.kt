package hu.bme.aut.android.trainingapp.home.plans

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.ActivityTrainingBinding
import hu.bme.aut.android.trainingapp.home.plans.exercise.ExerciseActivity
import hu.bme.aut.android.trainingapp.model.Exercise
import hu.bme.aut.android.trainingapp.model.Training
import hu.bme.aut.android.trainingapp.model.TrainingDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrainingActivity : AppCompatActivity(), PlansDataHolder , TrainingDaysAdapter.OnTrainingDaySelectedListener {

    private lateinit var binding: ActivityTrainingBinding
    private lateinit var database: DatabaseReference
    private var adapter = TrainingDaysAdapter(this)
    private lateinit var day: String
    private lateinit var type: String
    private lateinit var estimatedDuration: String
    private lateinit var training: Training
    private lateinit var dialog: Dialog

    private var trainingDays : ArrayList<TrainingDays> = arrayListOf()
    private var exercises : ArrayList<Exercise> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle : Bundle? = intent.extras
        val name = bundle?.getString("Name")
        binding.tvName.text = name
        database = FirebaseDatabase.getInstance().getReference("TrainingPlans")
        initRecycleView()
        showProgressBar()
        GlobalScope.launch(Dispatchers.IO) {
            delay(1000L)
            runOnUiThread{
                hideProgressBar()
                adapter.update(trainingDays)
            }

        }
        readData()



    }

    private fun initRecycleView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun readData() {
        database.child("TrainingPlanOne").child("trainingDays").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    trainingDays.clear()
                    var cnt = "0"
                    for (trainingSnapshot in snapshot.children) {
                        day = trainingSnapshot.child("day").value.toString()
                        training =
                            trainingSnapshot.child("training").getValue(Training::class.java)!!
                        type = trainingSnapshot.child("type").value.toString()
                        estimatedDuration =
                            trainingSnapshot.child("estimatedDuration").value.toString()
                        database.child("TrainingPlanOne").child("trainingDays").child(cnt)
                            .child("exercise").addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                exercises.clear()
                                for (exerciseSnapshot in snapshot.children) {
                                    val desc = snapshot.child("description").value.toString()
                                    val duration = snapshot.child("duration").value.toString()
                                    val exType = snapshot.child("type").value.toString()
                                    val exercise = Exercise(exType, duration, desc)
                                    exercises.add(exercise!!)
                                }
                                val trainingDay =
                                    TrainingDays(day, type, estimatedDuration, training, exercises)
                                trainingDays.add(trainingDay)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })

                        var num = cnt.toInt()
                        num = num.plus(1)
                        cnt = num.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onTrainingDaySelectedListener(position: Int) {
        val intent = Intent()
        intent.setClass(this@TrainingActivity, ExerciseActivity::class.java)
        intent.putExtra("position", position)
        intent.putExtra("Day", day)
        startActivity(intent)

    }

    override fun getTrainingDays(): ArrayList<TrainingDays> {
        return trainingDays
    }

    override fun getExercises(): ArrayList<Exercise> {
        return exercises
    }

    private fun showProgressBar(){
        dialog = Dialog(this@TrainingActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}