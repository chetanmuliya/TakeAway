package `in`.ecommerce.takeaway.view.ui.cart

import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Database.CartDataSource
import `in`.ecommerce.takeaway.Database.CartDatabase
import `in`.ecommerce.takeaway.Database.CartItem
import `in`.ecommerce.takeaway.Database.LocalCartDataSource
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CartViewModel : ViewModel() {

   private val compositeDisposable:CompositeDisposable
    private var cartDataSource:CartDataSource?=null
    private var mutableLiveDataCartItem:MutableLiveData<List<CartItem>>?=null

    init {
        compositeDisposable = CompositeDisposable()
    }
    fun initialCartDatSource(context: Context){
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

    fun onStop(){
        compositeDisposable.clear()
    }
    fun getmutableLiveDataCartItem():MutableLiveData<List<CartItem>>{

        if (mutableLiveDataCartItem == null)
            mutableLiveDataCartItem = MutableLiveData()
            getCartItems()
        return mutableLiveDataCartItem!!
    }
    private fun getCartItems() {
        compositeDisposable.addAll(cartDataSource!!.getAllCart(Common.current_user!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({cartItems ->
                mutableLiveDataCartItem!!.value = cartItems
            },{
                mutableLiveDataCartItem!!.value=null
            })
        )
    }
}