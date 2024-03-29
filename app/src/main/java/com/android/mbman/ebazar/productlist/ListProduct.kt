package com.android.mbman.ebazar.productlist

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.mbman.ebazar.R
import com.android.mbman.ebazar.databinding.FragmentListProductBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class ListProductFragment : Fragment(R.layout.fragment_list_product),
    EasyPermissions.PermissionCallbacks {

    private lateinit var productBinding: FragmentListProductBinding
    private lateinit var imageUri: Uri
    private lateinit var addProductDialog: Dialog
    private lateinit var listProductAdapter: ListProductAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var progressDialog: ProgressDialog
    private lateinit var productImage: ImageView

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val res = result.data
                if (res != null) {
                    imageUri = res.data!!
                    productImage.setImageURI(imageUri)
                    Log.d("image uri", "IMAGE URI: $imageUri")
                }


                val addIcon: ImageView = addProductDialog.findViewById(R.id.addIcon)
                val text: TextView = addProductDialog.findViewById(R.id.add_product_text)
                text.isVisible = false
                addIcon.isVisible = false
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_product, container, false)
        return productBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val query = FirebaseDatabase.getInstance()
            .reference
            .child("Product")
        val options = FirebaseRecyclerOptions.Builder<ProductModel>()
            .setQuery(query, ProductModel::class.java)
            .setLifecycleOwner(this)
            .build()

        productBinding.listProductRV.layoutManager = LinearLayoutManager(requireContext())
        listProductAdapter = ListProductAdapter(requireContext(), options)

        listProductAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                // Data has been loaded, hide the progress bar and show the RecyclerView
                productBinding.loadingProgressBar.visibility = View.GONE
                productBinding.listProductRV.visibility = View.VISIBLE
            }
        })

        productBinding.listProductRV.adapter = listProductAdapter
        listProductAdapter.notifyDataSetChanged()

        productBinding.addProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun showAddProductDialog() {
        addProductDialog = Dialog(requireContext())
        addProductDialog.apply {
            window?.setContentView(R.layout.add_product)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(true)
        }.create()

        val productName: EditText = addProductDialog.findViewById(R.id.add_product_name)
        val productPrice: EditText = addProductDialog.findViewById(R.id.add_product_price)
        val productDesc: EditText = addProductDialog.findViewById(R.id.add_product_description)
        val addProductBTN: Button = addProductDialog.findViewById(R.id.add_product_button)
        productImage = addProductDialog.findViewById(R.id.add_product_image)

        val spinner: Spinner = addProductDialog.findViewById(R.id.spinner)

        val items = listOf("Select Item", "Electronic", "Electrical", "Agricultural", "Garments")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)



        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner.adapter = adapter
        val defaultSelectedItemPosition = 0
        spinner.setSelection(defaultSelectedItemPosition)
        var selectedCategory = ""

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    selectedCategory = items[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        productImage.setOnClickListener {
            checkStoragePermission()
        }

        addProductBTN.setOnClickListener {
            val name = productName.text.toString()
            val price = productPrice.text.toString()
            val description = productDesc.text.toString()
            val imageUri = imageUri

            Log.d("ON CLICK ADD PRODUCT", "showAddProductDialog: $")

            uploadToFirebase(
                imageUri,
                requireContext(),
                name,
                price,
                description,
                selectedCategory,
                firebaseAuth.uid.toString()
            )


        }

        addProductDialog.show()

    }


    private fun pickImage() {
        val pickImageIntent = Intent(Intent.ACTION_PICK)
        pickImageIntent.type = "image/*"
        launcher.launch(pickImageIntent)
    }


    fun uploadToFirebase(
        imageUri: Uri,
        context: Context,
        name: String,
        price: String,
        description: String,
        selectedCategory: String,
        uid: String
    ) {

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Adding the Product!")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val randomText = generateRandomText(4)
        val user = firebaseAuth.currentUser
        val storageReference = storage.reference
            .child("product_images $randomText")
            .child(user!!.uid)

        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                getDownloadImageUrl(
                    storageReference,
                    context,
                    name,
                    price,
                    description,
                    selectedCategory,
                    uid
                )
            }
            .addOnProgressListener { snapshot ->
                val progress = 100 * snapshot.bytesTransferred / snapshot.totalByteCount
                progressDialog.setMessage("$progress%")

            }

    }

    private fun getDownloadImageUrl(
        reference: StorageReference,
        context: Context,
        name: String,
        price: String,
        description: String,
        selectedCategory: String,
        uid: String
    ) {
        // This method will download the image's Url from firebase and set the userProfile.
        reference.downloadUrl.addOnSuccessListener { uri ->
            val productModel = ProductModel(
                uri.toString(),
                name,
                price,
                description,
                selectedCategory,
                firebaseAuth.uid.toString()
            )

            FirebaseDatabase.getInstance().reference.child("Product").push().setValue(productModel)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Product Added !", Toast.LENGTH_SHORT).show()
                    addProductDialog.dismiss()
                    progressDialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Product Addition Failed !",
                        Toast.LENGTH_SHORT
                    ).show()
                    addProductDialog.dismiss()
                    progressDialog.dismiss()
                }
        }
    }


    private fun hasStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun checkStoragePermission() {
        if (hasStoragePermission()) {
            pickImage()
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.storage_rationale),
                STORAGEPERMISSIONCODE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun generateRandomText(length: Int): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        pickImage()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    companion object {
        const val STORAGEPERMISSIONCODE = 101

    }

    override fun onStart() {
        super.onStart()
        listProductAdapter.notifyDataSetChanged();
        listProductAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        listProductAdapter.stopListening()
    }
}
