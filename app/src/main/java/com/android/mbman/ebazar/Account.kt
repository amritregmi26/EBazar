package com.android.mbman.ebazar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.android.mbman.ebazar.R.layout
import com.android.mbman.ebazar.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_account.view.user_image

class AccountFragment : Fragment(layout.fragment_account)
{
    private lateinit var accountBinding: FragmentAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        accountBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container,false)
        return accountBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val userEmail = firebaseAuth.currentUser!!.email

        val docRef = db.collection("Users").document(userEmail!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if(document != null)
                {
                    val userName = document.get("name").toString()
                    val userAddress = document.get("address").toString()
                    val userPhoneNumber = document.get("phoneNumber").toString()

                    accountBinding.email.setText(userEmail)
                    accountBinding.fullName.setText(userName)
                    accountBinding.address.setText(userAddress)
                    accountBinding.phoneNumber.setText(userPhoneNumber)
                }
            }
            .addOnFailureListener() { exception ->
                Toast.makeText(requireActivity(), "Some Error occured while fetching the data from database", Toast.LENGTH_SHORT).show()
            }
    }
}