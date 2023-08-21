package com.android.mbman.ebazar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.mbman.ebazar.databinding.RegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupController : AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore

    override fun onStart() {
        super.onStart()
       /* auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this@SignupController, MainActivity::class.java))
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseAuth Initialization
        auth = FirebaseAuth.getInstance()

        // Redirecting the user to the login screen
        binding.loginLink.setOnClickListener {
            val intent = Intent(this, LoginController::class.java)
            startActivity(intent)
            finish()
        }

        // Processes to be followed after clicking the sign up button
        binding.signUpBtn.setOnClickListener {
            if (checking()) {
                val email = binding.email.text.toString()
                val address = binding.address.text.toString()
                val fullName = binding.fullName.text.toString()
                val phoneNumber = binding.phoneNumber.text.toString()
                val password = binding.password.text.toString()
                val confirmPass = binding.confirmPassword.text.toString()

                // Creating Hash Map of user data to store it to the database
                val userData = hashMapOf(
                    "name" to fullName,
                    "address" to address,
                    "email" to email,
                    "phoneNumber" to phoneNumber
                )

                if (password == confirmPass) {
                    if (password.length <= 5) {
                        Toast.makeText(
                            this,
                            "Password must be at least 6 characters long",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val users = db.collection("Users")
                        users.document(email).get()
                            .addOnSuccessListener { task ->
                                if (task.exists()) {
                                    Toast.makeText(
                                        this,
                                        "User Already Registered",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent = Intent(this, LoginController::class.java)
                                    startActivity(intent)
                                } else {
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this) { task ->
                                            if (task.isSuccessful) {
                                                users.document(email).set(userData)
                                                val intent = Intent(this, MainActivity::class.java)
                                                intent.putExtra("email", email)
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "Authentication Failed",
                                                    Toast.LENGTH_LONG
                                                )
                                            }
                                        }
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Password Did Not Match !!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are Not Allowed!!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checking(): Boolean {
        if (binding.email.text.trim { it <= ' ' }.isNotEmpty() &&
            binding.address.text.trim { it <= ' ' }.isNotEmpty() &&
            binding.fullName.text.trim { it <= ' ' }.isNotEmpty() &&
            binding.phoneNumber.text.trim { it <= ' ' }.isNotEmpty() &&
            binding.password.text.trim { it <= ' ' }.isNotEmpty() &&
            binding.confirmPassword.text.trim { it <= ' ' }.isNotEmpty()
        ) {
            return true
        }

        return false
    }
}