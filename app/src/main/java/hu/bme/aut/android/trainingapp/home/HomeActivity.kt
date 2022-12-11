package hu.bme.aut.android.trainingapp.home

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import hu.bme.aut.android.trainingapp.BaseActivity
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.ActivityHomeBinding
import hu.bme.aut.android.trainingapp.home.friends.FriendsFragment
import hu.bme.aut.android.trainingapp.home.home.HomeFragment
import hu.bme.aut.android.trainingapp.home.plans.PlansFragment
import hu.bme.aut.android.trainingapp.home.profile.ProfileFragment
import hu.bme.aut.android.trainingapp.home.settings.SettingsFragment
import hu.bme.aut.android.trainingapp.home.settings.language.MyContextWrapper
import hu.bme.aut.android.trainingapp.home.settings.language.MyPreference
import hu.bme.aut.android.trainingapp.model.*
import java.io.File

class HomeActivity : BaseActivity(), UserDataHolder {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private var loaded = false
    private lateinit var myPreference: MyPreference
    private lateinit var user: User
    private lateinit var bitmap: Bitmap
    private lateinit var email: String
    private lateinit var date: String




    private lateinit var storageReference : StorageReference
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle : Bundle? = intent.extras
        val first = bundle?.getString("First")
        val second = bundle?.getString("Second")
        val userName = bundle?.getString("User")
        val age = bundle?.getString("Age")
        val phone = bundle?.getString("Phone")
        val gender = bundle?.getString("Gender")
        val trainings = bundle?.getParcelableArrayList<Training>("Trainings")
        val friends = bundle?.getParcelableArrayList<Friend>("Friends")
        val skip = bundle?.getString("Skip")
        val calories = bundle?.getString("CaloriesBurnt")
        val distance = bundle?.getString("DistanceTraveled")
        val hours = bundle?.getString("HoursSpent")
        val training = bundle?.getString("TrainingsDone")
        loaded = bundle?.getBoolean("Loaded")!!
        email = bundle.getString("Email").toString()
        date = bundle.getString("Date").toString()
        user = User(Name(first, second), userName, age, phone, gender, skip,
            trainings as ArrayList<Training>,
            friends as ArrayList<Friend>, TrainingDetails(distance, calories, hours, training))
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("Users/"+auth.currentUser?.uid)
        loadPicture()
        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val settingsFragment = SettingsFragment()
        val friendsFragment = FriendsFragment()
        val plansFragment = PlansFragment()
        setCurrentFragment(homeFragment)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.miHome -> setCurrentFragment(homeFragment)
                R.id.miCalendar -> setCurrentFragment(plansFragment)
                R.id.miFriends -> setCurrentFragment(friendsFragment)
                R.id.miProfile -> setCurrentFragment(profileFragment)
                R.id.miSettings -> setCurrentFragment(settingsFragment)
            }
            true
        }


    }

    override fun attachBaseContext(newBase: Context?) {
        myPreference = MyPreference(newBase!!)
        val lang = myPreference.getLoginCount()
        super.attachBaseContext(MyContextWrapper.wrap(newBase, lang.toString()))
    }



    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(binding.flFragment.id, fragment)
            commit()
        }

    override fun getLoaded(): Boolean {
        return loaded
    }

    override fun getUser(): User {
        return user
    }

    override fun getBitmap(): Bitmap {
        return bitmap
    }

    override fun getEmail(): String {
        return email
    }

    override fun setDate(date2: String) {
        date = date2
    }

    override fun getDate(): String {
        return date
    }

    private fun loadPicture() {
        showProgressBar()
        val localFile = File.createTempFile("tempFile", ".jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            hideProgressBar()
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        }.addOnFailureListener{
            hideProgressBar()

            Toast.makeText(this@HomeActivity, "Failed to get the image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgressBar(){
        dialog = Dialog(this@HomeActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}