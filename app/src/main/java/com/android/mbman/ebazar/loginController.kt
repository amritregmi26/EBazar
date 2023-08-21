package com.android.mbman.ebazar

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.mbman.ebazar.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginController : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerLink.setOnClickListener {
            val intent = Intent(this, SignupController::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener {
            if (binding.email != null) {
                val email = binding.email!!.text.toString().trim()
                val password = binding.loginPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "Empty Fields are not allowed !!!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}