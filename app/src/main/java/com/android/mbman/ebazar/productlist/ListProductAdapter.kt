package com.android.mbman.ebazar.productlist

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.mbman.ebazar.R
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListProductAdapter(
    private val context: Context,
    options: FirebaseRecyclerOptions<ProductModel>
) :
    FirebaseRecyclerAdapter<ProductModel, ListProductAdapter.ProductViewHolder>(options) {

    private lateinit var detailsDialog: Dialog
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_productlist, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: ProductModel) {

        val query = FirebaseDatabase.getInstance()
            .reference
            .child("Product")

        if (model.uid == firebaseAuth.uid.toString()) {
            holder.productName.text = model.productName
            holder.productPrice.text = model.productPrice
            holder.product_category.text = model.category
            holder.delete.isVisible = true

            holder.delete.setOnClickListener {
                val key = getRef(position).key
                FirebaseDatabase.getInstance()
                    .reference
                    .child("Product").child(key!!).removeValue()
            }

            Log.d("PRODUCT NAME = ", "onBindViewHolder: ${model.productName}")
            Log.d("PRODUCT NAME = ", "onBindViewHolder: ${model.productPrice}")
            Log.d("PRODUCT NAME = ", "onBindViewHolder: ${model.productDescription}")

            // Load product image using Glide or your preferred image loading library
            Glide.with(context)
                .load(model.productImage)
                .into(holder.productImage)

            holder.productCard.setOnClickListener {
                showDetailsCard(model = model)
            }
        } else {
            holder.productCard.isVisible = false
        }
    }

    private fun checkAndDeleteProduct(productNameToDelete: String) {
        val query =
            FirebaseDatabase.getInstance().getReference("Products").orderByChild("productName")
                .equalTo(productNameToDelete)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Deleted Successfully!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Deletion Failed!", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }


    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productCard: CardView = itemView.findViewById(R.id.prodcutCard)
        val product_category: TextView = itemView.findViewById(R.id.product_category)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    private fun showDetailsCard(model: ProductModel) {
        detailsDialog = Dialog(context)
        detailsDialog.apply {
            window?.setContentView(R.layout.show_details)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(true)
        }.create()

        val productName: TextView = detailsDialog.findViewById(R.id.productName)
        val productPrice: TextView = detailsDialog.findViewById(R.id.productPrice)
        val productDesc: TextView = detailsDialog.findViewById(R.id.productDescription)
        val productCategory: TextView = detailsDialog.findViewById(R.id.productCategory)
        val productImage: ImageView = detailsDialog.findViewById(R.id.productImage)

        productName.text = model.productName
        productPrice.text = model.productPrice
        productDesc.text = model.productDescription
        productCategory.text = model.category

        Glide.with(context)
            .load(model.productImage)
            .into(productImage)

        detailsDialog.show()
    }

}
