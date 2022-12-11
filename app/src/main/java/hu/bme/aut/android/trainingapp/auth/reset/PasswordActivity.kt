package hu.bme.aut.android.trainingapp.auth.reset

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.android.trainingapp.BaseActivity
import hu.bme.aut.android.trainingapp.databinding.ActivityPasswordBinding

class PasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnEmail.setOnClickListener{
            resetPassword()
        }
    }

    private fun resetPassword(){
        val email: String = binding.etEmail.text.toString()
        if(email.isEmpty()){
            Toast.makeText(
                this@PasswordActivity,
                "Please enter your email address!",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(
                            this@PasswordActivity,
                            "Email sent successfully to your email account!",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    } else{
                        Toast.makeText(
                            this@PasswordActivity,
                            task.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }
}