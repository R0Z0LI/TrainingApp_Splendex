package hu.bme.aut.android.trainingapp.home.home

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.FragmentHomeBinding
import hu.bme.aut.android.trainingapp.home.UserDataHolder
import hu.bme.aut.android.trainingapp.model.Training
import hu.bme.aut.android.trainingapp.model.User
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home), DatePickerDialog.OnDateSetListener, UserAdapter.OnItemClickListener{


    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user : User
    private var pos = -1
    private lateinit var trainingRecyclerView: RecyclerView
    private var trainingList : ArrayList<Training> = arrayListOf()
    private var userDataHolder: UserDataHolder? = null

    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH)

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trainingRecyclerView = binding.recyclerView
        trainingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        trainingRecyclerView.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()

        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        val addFragment = AddFragment()
        getData()
        initRecyclerView()

        binding.tvDatePicker.text = SimpleDateFormat("EEEE, MMMM dd, yyyy").format(Date())

        binding.tvDatePicker.setOnClickListener {
            val calendar2 = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = calendar2.timeInMillis
            datePicker.show()

        }

        binding.btnAdd.setOnClickListener{
            transaction.replace(R.id.flFragment, addFragment)
            transaction.commit()
        }



    }

    private fun getData() {
        user = userDataHolder?.getUser()!!
        trainingList.clear()
        val trainings = user.trainings
        if(trainings != null){
            for(Training in trainings){
                if(Training.date.equals(formatter.format(calendar.timeInMillis))){
                    trainingList.add(Training)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val name = data?.getStringExtra("Name")
        val type = data?.getStringExtra("Type")
        val date = data?.getStringExtra("Date")
        val avgPulse = data?.getStringExtra("AvgPulse")
        val maxPulse = data?.getStringExtra("MaxPulse")
        val duration = data?.getStringExtra("Duration")
        val distance = data?.getStringExtra("Distance")
        val avgSpeed = data?.getStringExtra("AvgSpeed")
        val maxSpeed = data?.getStringExtra("MaxSpeed")
        val calories = data?.getStringExtra("Calories")
        val training = Training(name, type, date, duration, calories, distance, avgSpeed, maxSpeed, avgPulse, maxPulse)
        trainingList[pos] = training
        user.trainings[pos] = training
        updateData()
        trainingRecyclerView.adapter?.notifyItemChanged(pos)
    }

    private fun initRecyclerView(){
        trainingRecyclerView.adapter = UserAdapter(trainingList, this)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        getData()
        initRecyclerView()
        userDataHolder?.setDate(formatter.format(calendar.timeInMillis))
        binding.tvDatePicker.text = formatter.format(calendar.timeInMillis)
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(requireContext(), TrainingDetailsActivity::class.java)
        pos = position
        intent.putExtra("Name", trainingList[position].name)
        intent.putExtra("Type", trainingList[position].type)
        intent.putExtra("Date", trainingList[position].date)
        intent.putExtra("AvgPulse", trainingList[position].avgPulse)
        intent.putExtra("MaxPulse", trainingList[position].maxPulse)
        intent.putExtra("Calories", trainingList[position].calories)
        intent.putExtra("Duration", trainingList[position].duration)
        intent.putExtra("Distance", trainingList[position].distance)
        intent.putExtra("AvgSpeed", trainingList[position].avgSpeed)
        intent.putExtra("MaxSpeed", trainingList[position].maxSpeed)
        startActivityForResult(intent, targetRequestCode)
    }

    override fun onItemLongClick(position: Int) {
        val training = trainingList.removeAt(position)
        var num = user.trainingDetails?.trainingsDone?.toInt()
        num = num?.minus(1)
        var distanceTraveled = user.trainingDetails?.distanceTraveled?.toInt()
        distanceTraveled = distanceTraveled?.minus(training.distance!!.toInt())
        var caloriesBurnt = user.trainingDetails?.caloriesBurnt?.toInt()
        caloriesBurnt = caloriesBurnt?.minus(training.calories!!.toInt())
        var hoursSpent = user.trainingDetails?.hoursSpent?.toInt()
        hoursSpent = hoursSpent?.minus(training.duration!!.toInt())
        user.trainingDetails?.trainingsDone = num.toString()
        user.trainingDetails?.distanceTraveled = distanceTraveled.toString()
        user.trainingDetails?.caloriesBurnt = caloriesBurnt.toString()
        user.trainingDetails?.hoursSpent = hoursSpent.toString()
        user.trainings.remove(training)
        updateData()
        trainingRecyclerView.adapter?.notifyItemRemoved(position)

    }

    private fun updateData() {
        val uid = auth.currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference("Users")
        if(uid != null){
            database.child(uid).setValue(user).addOnCompleteListener{
                if(it.isSuccessful){

                } else {

                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}