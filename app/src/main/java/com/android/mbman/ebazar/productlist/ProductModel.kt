package com.android.mbman.ebazar.productlist

data class ProductModel(
    var productImage: String,
    var productName: String,
    var productPrice: String,
    var productDescription: String
) {
    constructor() : this("", "", "", "")
}