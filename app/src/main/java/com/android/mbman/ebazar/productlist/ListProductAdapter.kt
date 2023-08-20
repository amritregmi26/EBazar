package com.android.mbman.ebazar.productlist

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.mbman.ebazar.R
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class ListProductAdapter(
    private val context: Context,
    options: FirebaseRecyclerOptions<ProductModel>
) :
    FirebaseRecyclerAdapter<ProductModel, ListProductAdapter.ProductViewHolder>(options) {

    private lateinit var detailsDialog: Dialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_productlist, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: ProductModel) {

        holder.productName.text = model.productName
        holder.productPrice.text = model.productPrice
        holder.product_category.text = model.category

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

}
