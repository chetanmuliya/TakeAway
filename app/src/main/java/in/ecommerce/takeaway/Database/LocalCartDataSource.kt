package `in`.ecommerce.takeaway.Database

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class LocalCartDataSource(private val carDAO:CartDAO):CartDataSource {
    override fun getAllCart(uid: String): Flowable<List<CartItem>> {
        return carDAO.getAllCart(uid)
    }

    override fun countItemInCart(uid: String): Single<Int> {
        return carDAO.countItemInCart(uid)
    }

    override fun sumPrice(uid: String): Single<Double> {
        return carDAO.sumPrice(uid)
    }

    override fun getItemInCart(foodid: String, uid: String): Flowable<List<CartItem>> {
        return carDAO.getItemInCart(foodid,uid)
    }

    override fun insertOrReplaceAll(vararg cartItem: CartItem): Completable {
       return carDAO.insertOrReplaceAll(*cartItem)
    }

    override fun updateCart(cartItem: CartItem): Single<Int> {
        return carDAO.updateCart(cartItem)
    }

    override fun deleteCart(cartItem: CartItem): Single<Int> {
        return carDAO.deleteCart(cartItem)
    }

    override fun cleanCart(uid: String): Single<Int> {
        return carDAO.cleanCart(uid)
    }

    override fun getItemsWithAllOptionsInCart(
        uid: String,
        foodid: String,
        foodsize: String,
        foodAddon: String
    ): Single<CartItem> {
        return carDAO.getItemWithAllOptionsInCart(uid,foodid,foodsize,foodAddon)
    }
}