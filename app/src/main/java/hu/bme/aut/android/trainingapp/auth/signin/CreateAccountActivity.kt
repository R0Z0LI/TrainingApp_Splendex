package hu.bme.aut.android.trainingapp.auth.signin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import hu.bme.aut.android.trainingapp.BaseActivity
import hu.bme.aut.android.trainingapp.auth.login.LoginActivity
import hu.bme.aut.android.trainingapp.databinding.ActivityCreateAccountBinding

class CreateAccountActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener{
            registerClick()
        }
    }

    private fun validateForm() = binding.etEmail.validateNonEmpty() && binding.etPassword.validateNonEmpty()

    fun EditText.validateNonEmpty(): Boolean {
        if (text.isEmpty()) {
            error = "Required"
            return false
        }
        return true
    }

    private fun registerClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        firebaseAuth
            .createUserWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            .addOnSuccessListener { result ->
                hideProgressDialog()
                sendVerificationEmail()

                val firebaseUser = result.user
                val profileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(firebaseUser?.email?.substringBefore('@'))
                    .build()
                firebaseUser?.updateProfile(profileChangeRequest)
                startActivity(Intent(this, LoginActivity::class.java))
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()

                toast(exception.message)
            }
    }

    private fun sendVerificationEmail(){

        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener(this) { task -> if(task.isSuccessful){
            Log.d(TAG, "Email sent")
        }
        }
            ?.addOnFailureListener { exception ->
                hideProgressDialog()

                toast(exception.message)
            }
    }

}