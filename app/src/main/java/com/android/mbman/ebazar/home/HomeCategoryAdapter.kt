package com.android.mbman.ebazar.home

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.mbman.ebazar.R
import com.android.mbman.ebazar.home.HomeFragment.Companion.CATEGORY
import com.android.mbman.ebazar.productlist.ProductModel
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth

class HomeCategoryAdapter(
    private val context: Context,
    options: FirebaseRecyclerOptions<ProductModel>
) :
    FirebaseRecyclerAdapter<ProductModel, HomeCategoryAdapter.ProductViewHolder>(options) {

    private lateinit var detailsDialog: Dialog
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_productlist, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: ProductModel) {

        if (model.uid != auth.uid.toString()) {
            when (CATEGORY) {
                "ALL" -> {
                    bindProductData(holder,model)
                }

                model.category -> {
                    bindProductData(holder,model)
                }

                else -> {
                    holder.productCard.isVisible = false
                }
            }

        } else {
            holder.productCard.isVisible = false
        }
    }


    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productCard: CardView = itemView.findViewById(R.id.prodcutCard)
        val product_category: TextView = itemView.findViewById(R.id.product_category)
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

    private fun bindProductData(holder: ProductViewHolder, model: ProductModel) {
        holder.productName.text = model.productName
        holder.productPrice.text = model.productPrice
        holder.product_category.text = model.category

        Log.d("PRODUCT NAME = ", "onBindViewHolder: ${model.productName}")
        Log.d("PRODUCT NAME = ", "onBindViewHolder: ${model.productPrice}")
        Log.d("PRODUCT NAME = ", "onBindViewHolder: ${model.productDescription}")
        Log.d("PHOTO NAME = ", "onBindViewHolder: ${model.productImage}")

        // Load product image using Glide or your preferred image loading library
        Glide.with(context)
            .load(model.productImage)
            .into(holder.productImage)

        holder.productCard.setOnClickListener {
            showDetailsCard(model = model)
        }
    }
}