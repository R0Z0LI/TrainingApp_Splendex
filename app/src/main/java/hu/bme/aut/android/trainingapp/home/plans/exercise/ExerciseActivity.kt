package hu.bme.aut.android.trainingapp.home.plans.exercise

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.ActivityExerciseBinding
import hu.bme.aut.android.trainingapp.model.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExerciseActivity : AppCompatActivity(), ExerciseAdapter.OnExerciseSelectedListener {

    private var position = -1
    private lateinit var day : String
    private lateinit var binding: ActivityExerciseBinding
    private lateinit var database: DatabaseReference
    private var exercises : ArrayList<Exercise> = arrayListOf()
    private var adapter = ExerciseAdapter(this)
    private lateinit var desc : String
    private lateinit var alertDialog: AlertDialog.Builder
    private lateinit var duration : String
    private lateinit var exType : String
    private lateinit var exercise : Exercise
    private lateinit var dialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle : Bundle? = intent.extras
        position = bundle?.getInt("position")!!
        day = bundle.getString("Day")!!
        database = FirebaseDatabase.getInstance().getReference("TrainingPlans")
        binding.tvDay.text = day
        initRecycleView()
        showProgressBar()
        GlobalScope.launch(Dispatchers.IO) {
            delay(2000L)
            runOnUiThread{
                hideProgressBar()
                adapter.update(exercises)
            }

        }
        binding.btnDone.setOnClickListener{

        }
        readData()
    }

    private fun initRecycleView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun readData() {
        database.child("TrainingPlanOne").child("trainingDays").child("0")
            .child("exercise").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    exercises.clear()
                    for (exerciseSnapshot in snapshot.children) {
                        desc = exerciseSnapshot.child("description").value.toString()
                        duration = exerciseSnapshot.child("duration").value.toString()
                        exType = exerciseSnapshot.child("type").value.toString()
                        exercise = Exercise(exType, duration, desc)
                        exercises.add(exercise)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onExerciseSelectedListener(position: Int) {
        alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Description")
            .setMessage(exercises[position].description)
            .show()
    }

    private fun showProgressBar(){
        dialog = Dialog(this@ExerciseActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}