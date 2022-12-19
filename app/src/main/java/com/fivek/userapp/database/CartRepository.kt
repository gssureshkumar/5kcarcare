package com.fivek.userapp.database

import androidx.lifecycle.LiveData

class CartRepository(private val cartDao: CartDao) {

    val cartList: LiveData<List<CartModelData>> = cartDao.getAllCartData()


    fun insert(cart: CartModelData) {
        cartDao.insert(cart)
    }

    fun insert(cart: List<CartModelData>) {
        cartDao.insert(cart)
    }

    fun delete(cartId: Int) {
        cartDao.delete(cartId)
    }

    fun deleteAll() {
        cartDao.deleteAll()
    }
}