package hu.bme.aut.android.trainingapp.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.android.trainingapp.BaseActivity
import hu.bme.aut.android.trainingapp.auth.reset.PasswordActivity
import hu.bme.aut.android.trainingapp.auth.signin.CreateAccountActivity
import hu.bme.aut.android.trainingapp.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser != null){
            startActivity(Intent(this, DetailsActivity::class.java))
            finish()
        }


        binding.tvForgotPassword.setOnClickListener{
            startActivity(Intent(this, PasswordActivity::class.java))
        }

        binding.tvAccountCreate.setOnClickListener{
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }

        binding.btnLogIn.setOnClickListener{
            loginClick()
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

    private fun loginClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()
        firebaseAuth
            .signInWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            .addOnSuccessListener {
                hideProgressDialog()
                if(firebaseAuth.currentUser?.isEmailVerified == true) {
                    startActivity(Intent(this, DetailsActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()

                toast(exception.localizedMessage)
            }
    }


}