package hu.bme.aut.android.trainingapp.auth.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import hu.bme.aut.android.trainingapp.BaseActivity
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.ActivityDetailsBinding
import hu.bme.aut.android.trainingapp.home.HomeActivity
import hu.bme.aut.android.trainingapp.model.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DetailsActivity : BaseActivity(){

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var imageUri: Uri
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference : StorageReference
    private lateinit var dialog: Dialog

    private var loaded = false
    private lateinit var user: User
    private lateinit var bitmap: Bitmap
    private lateinit var email: String
    private lateinit var date: String
    private lateinit var firstName : String
    private lateinit var secondName : String
    private lateinit var userName2 : String
    private lateinit var gender : String
    private lateinit var age : String
    private lateinit var phoneNumber : String
    private lateinit var skip : String
    private var distanceTraveled : String = "0"
    private var hoursSpent : String = "0"
    private var caloriesBurnt : String = "0"
    private var trainingsDone : String = "0"
    var trainings : ArrayList<Training> = arrayListOf()
    var friends : ArrayList<Friend> = arrayListOf()

    private var selected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bitmap = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888)
        auth = FirebaseAuth.getInstance()
        email = auth.currentUser?.email.toString()
        storageReference = FirebaseStorage.getInstance().getReference("Users/"+auth.currentUser?.uid)
        date = SimpleDateFormat("EEEE, MMMM dd, yyyy").format(Date())

        loadData()

        loadPicture()
        storageReference = FirebaseStorage.getInstance().getReference("Users")

        binding.btnNext.setOnClickListener {
            saveData(auth)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            var byte : ByteArray = stream.toByteArray()
            startAct()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("First", firstName)
            intent.putExtra("Second", secondName)
            intent.putExtra("User", userName2)
            intent.putExtra("Age", age)
            intent.putExtra("Phone", phoneNumber)
            intent.putExtra("Gender", gender)
            intent.putExtra("Trainings", trainings)
            intent.putExtra("Friends", friends)
            intent.putExtra("Skip", skip)
            intent.putExtra("CaloriesBurnt", caloriesBurnt)
            intent.putExtra("DistanceTraveled", distanceTraveled)
            intent.putExtra("HoursSpent", hoursSpent)
            intent.putExtra("TrainingsDone", trainingsDone)
            intent.putExtra("Email", email)
            intent.putExtra("Bitmap", byte)
            intent.putExtra("Date", date)
            intent.putExtra("Loaded", loaded)
            startActivity(intent)

        }

        binding.tvProfilePicture.setOnClickListener {
            selectImage()
        }

        binding.spGenders.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                gender = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun startAct(){

    }

    private fun saveData(auth : FirebaseAuth) {

        val uid = auth.currentUser?.uid
        firstName = binding.etFirstName.text.toString()
        secondName = binding.etSecondName.text.toString()
        userName2 = binding.etUserName.text.toString()
        val name = Name(firstName, secondName)
        phoneNumber = binding.etPhoneNumber.text.toString()
        age = binding.etAge.text.toString()
        skip = "false"
        if(binding.checkBox.isChecked){
            skip = "true"
        }

        user = User(name, userName, phoneNumber, age, gender, skip, trainings, friends, TrainingDetails(distanceTraveled, caloriesBurnt, hoursSpent, trainingsDone))
        if(uid != null){
            database.child(uid).setValue(user).addOnCompleteListener{
                if(it.isSuccessful){

                } else {
                    hideProgressBar()
                    Toast.makeText(this@DetailsActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadProfilePic() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            storageReference.child(uid).putFile(imageUri).addOnSuccessListener {
                hideProgressBar()
                Toast.makeText(this@DetailsActivity, "Successfully updated profile picture", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                hideProgressBar()
                Toast.makeText(this@DetailsActivity, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectImage(){
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && data != null){
            imageUri = data.data!!
            selected = true
            loaded = true
            binding.circleImageView.setImageURI(imageUri)
            showProgressBar()
            uploadProfilePic()
        }
    }

    private fun showProgressBar(){
        dialog = Dialog(this@DetailsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }

    private fun loadData() {
        showProgressBar()
        val uid = auth.currentUser?.uid

        if(uid != null){
            Toast.makeText(this@DetailsActivity, "OK", Toast.LENGTH_SHORT).show()
            readData(uid)
            loadPicture()
        }else{
            Toast.makeText(this@DetailsActivity, "Failed to find user", Toast.LENGTH_SHORT).show()
        }
    }


    private fun readData(uid: String) {

        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                firstName = it.child("name").child("firstName").value.toString()
                secondName = it.child("name").child("secondName").value.toString()
                userName2 = it.child("userName").value.toString()
                age = it.child("age").value.toString()
                phoneNumber = it.child("phoneNumber").value.toString()
                gender = it.child("sex").value.toString()
                skip = it.child("skip").value.toString()
                distanceTraveled = it.child("trainingDetails").child("distanceTraveled").value.toString()
                hoursSpent = it.child("trainingDetails").child("hoursSpent").value.toString()
                caloriesBurnt = it.child("trainingDetails").child("caloriesBurnt").value.toString()
                trainingsDone = it.child("trainingDetails").child("trainingsDone").value.toString()
                binding.etFirstName.setText(firstName)
                binding.etSecondName.setText(secondName)
                binding.etUserName.setText(userName.toString())
                binding.etAge.setText(age)
                binding.etPhoneNumber.setText(phoneNumber)


                database.child(uid).child("trainings").addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            trainings.clear()
                            for (trainingSnapshot in snapshot.children) {
                                val training = trainingSnapshot.getValue(Training::class.java)
                                trainings.add(training!!)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
                database.child(uid).child("friends").addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            friends.clear()
                            for (trainingSnapshot in snapshot.children) {
                                val friend = trainingSnapshot.getValue(Friend::class.java)!!
                                friends.add(friend)
                            }
                        }
                        user = User(Name(firstName, secondName), userName2, phoneNumber, age, gender, skip, trainings, friends, TrainingDetails(distanceTraveled, caloriesBurnt, hoursSpent, trainingsDone))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }else{
                Toast.makeText(this@DetailsActivity, "User does not exist", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(this@DetailsActivity, "Failed to read the data", Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadPicture() {
        val localFile = File.createTempFile("tempFile", ".jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            hideProgressBar()
            loaded = true
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.circleImageView.setImageBitmap(bitmap)
        }.addOnFailureListener{
            if(dialog.isShowing){
                hideProgressBar()
            }
            Toast.makeText(this@DetailsActivity, "Failed to get the image", Toast.LENGTH_SHORT).show()
        }


    }

}