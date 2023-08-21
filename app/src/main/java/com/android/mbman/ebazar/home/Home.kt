package com.android.mbman.ebazar.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.mbman.ebazar.R
import com.android.mbman.ebazar.databinding.FragmentHomeBinding
import com.android.mbman.ebazar.productlist.ListProductAdapter
import com.android.mbman.ebazar.productlist.ProductModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Arrays

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var homeCategoryAdapter: HomeCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return homeBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        homeBinding.userIcon.setOnClickListener {

            firebaseAuth.signOut()
        }

        homeBinding.categoryAll.setOnClickListener {
            CATEGORY = "ALL"
            homeBinding.categoryItemsRecyclerView.adapter = homeCategoryAdapter
            homeCategoryAdapter.notifyDataSetChanged()
        }
        homeBinding.categoryAgriculture.setOnClickListener {
            CATEGORY = "Agricultural"
            homeBinding.categoryItemsRecyclerView.adapter = homeCategoryAdapter
            homeCategoryAdapter.notifyDataSetChanged()
        }
        homeBinding.categoryElectronics.setOnClickListener {
            CATEGORY = "Electronic"
            homeBinding.categoryItemsRecyclerView.adapter = homeCategoryAdapter
            homeCategoryAdapter.notifyDataSetChanged()
        }
        homeBinding.categoryElectrial.setOnClickListener {
            CATEGORY = "Electrical"
            homeBinding.categoryItemsRecyclerView.adapter = homeCategoryAdapter
            homeCategoryAdapter.notifyDataSetChanged()
        }
        homeBinding.categoryGarments.setOnClickListener {
            CATEGORY = "Garments"
            homeBinding.categoryItemsRecyclerView.adapter = homeCategoryAdapter
            homeCategoryAdapter.notifyDataSetChanged()
        }



        val query = FirebaseDatabase.getInstance()
            .reference
            .child("Product")

        val options = FirebaseRecyclerOptions.Builder<ProductModel>()
            .setQuery(query, ProductModel::class.java)
            .setLifecycleOwner(this)
            .build()

        homeBinding.categoryItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        homeCategoryAdapter = HomeCategoryAdapter(requireContext(), options)

        homeCategoryAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                // Data has been loaded, hide the progress bar and show the RecyclerView
                homeBinding.loadingProgressBar.visibility = View.GONE
                homeBinding.categoryItemsRecyclerView.visibility = View.VISIBLE
            }
        })

        homeBinding.categoryItemsRecyclerView.adapter = homeCategoryAdapter

    }

    companion object{
        var CATEGORY = "ALL"
    }
}