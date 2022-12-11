package hu.bme.aut.android.trainingapp.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.FragmentProfileBinding
import hu.bme.aut.android.trainingapp.home.UserDataHolder

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
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
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(userDataHolder != null){
            initComponents()
        }
    }

    private fun initComponents() {
        val user = userDataHolder?.getUser()
        val bitmap = userDataHolder?.getBitmap()
        val email = userDataHolder?.getEmail()
        val loaded = userDataHolder?.getLoaded()
        binding.tvFirstName.text = user?.name?.firstName.toString()
        binding.tvSecondName.text = user?.name?.secondName.toString()
        binding.tvUserName.text = user?.userName.toString()
        if(loaded == true){
            binding.ivProfilePic.setImageBitmap(bitmap)
        } else{
            binding.ivProfilePic.setImageResource(R.drawable.profile)
        }
        binding.tvAge2.text = user?.age.toString()
        binding.tvPhoneNumber.text = user?.phoneNumber.toString()
        binding.tvEmail2.text = email
        binding.tvTrainingsDone.text = user?.trainingDetails?.trainingsDone.toString() + " trainings"
        binding.tvCaloriesBurnt.text = user?.trainingDetails?.caloriesBurnt.toString() + " calories"
        binding.tvDistanceTraveled.text = user?.trainingDetails?.distanceTraveled.toString() + " km"
        binding.tvHoursSpent.text = user?.trainingDetails?.hoursSpent.toString() + " hours"
    }
}