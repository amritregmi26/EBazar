package com.android.mbman.ebazar

import androidx.fragment.app.Fragment
import com.android.mbman.ebazar.databinding.FragmentAccountBinding
import kotlinx.android.synthetic.main.fragment_account.view.user_image

class AccountFragment : Fragment(R.layout.fragment_account)
{
    private lateinit var binding: FragmentAccountBinding
    //picUpdate function is used to update the profile picture of the user
    private fun picUpdate()
    {
        val image = binding.userImage
        val updateText = binding.updateText
        val updateIcon = binding.updateIcon

        updateText.setOnClickListener {
            imageSelection()
        }

        updateIcon.setOnClickListener {
            imageSelection()
        }
    }

    private fun imageSelection()
    {

    }

    private fun infoEdit()
    {

    }


}