package com.android.mbman.ebazar.productlist

data class ProductModel(
    var productImage: String,
    var productName: String,
    var productPrice: String,
    var productDescription: String,
    var category: String,
    var uid: String
) {
    constructor() : this("", "", "", "", "", "")
}