package `in`.ecommerce.takeaway.view.ui.cart

import `in`.ecommerce.takeaway.Adapter.MyCartAdapter
import `in`.ecommerce.takeaway.Callback.IMyButtonClick
import `in`.ecommerce.takeaway.Common.MySwipeHelper
import `in`.ecommerce.takeaway.Database.CartDataSource
import `in`.ecommerce.takeaway.Database.CartDatabase
import `in`.ecommerce.takeaway.Database.LocalCartDataSource
import `in`.ecommerce.takeaway.EventBus.CounterCartEvent
import `in`.ecommerce.takeaway.EventBus.HideCartFab
import `in`.ecommerce.takeaway.EventBus.UpdateItemsInCart
import `in`.ecommerce.takeaway.R
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.os.RecoverySystem
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.internal.FallbackServiceBroker
import com.google.android.gms.common.internal.service.Common
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CartFragment : Fragment() {

    private var adapter: MyCartAdapter?=null
    private lateinit var cartViewModel: CartViewModel
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var cartDataSource: CartDataSource?=null
    private var recyclerViewState: Parcelable?=null

    var txt_total_price:TextView?=null
    var txt_empty_cart:TextView?=null
    var groupPlace_holder:CardView?=null
    var recyclerCart:RecyclerView?=null

    override fun onResume() {
        super.onResume()
        calculateTotalPrice()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cartViewModel =
            ViewModelProviders.of(this).get(CartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cart, container, false)

        cartViewModel.initialCartDatSource(context!!)
        EventBus.getDefault().postSticky(HideCartFab(true))
        initView(root)

        cartViewModel.getmutableLiveDataCartItem().observe(this, Observer {
            if (it == null || it.isEmpty()){
                recyclerCart!!.visibility = View.GONE
                groupPlace_holder!!.visibility = View.GONE
                txt_empty_cart!!.visibility = View.VISIBLE
            }else{
                recyclerCart!!.visibility = View.VISIBLE
                groupPlace_holder!!.visibility = View.VISIBLE
                txt_empty_cart!!.visibility = View.GONE

                adapter = MyCartAdapter(context!!,it)
                recyclerCart!!.adapter = adapter
            }
        })
        return root
    }

    private fun initView(root: View?) {
        setHasOptionsMenu(true)
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context!!).cartDAO())
        recyclerCart = root!!.findViewById(R.id.recycler_cart) as RecyclerView
        recyclerCart!!.setHasFixedSize(true)
        val layoutmanager = LinearLayoutManager(context);
        recyclerCart!!.layoutManager = layoutmanager
        recyclerCart!!.addItemDecoration(DividerItemDecoration(context,layoutmanager.orientation))


        val swipe = object :MySwipeHelper(context!!,recyclerCart!!,200){
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(context!!,"Delete",30,0,Color.parseColor("#FF3C30"),
                object :IMyButtonClick{
                    override fun onClick(pos: Int) {
                       val deleteItem = adapter!!.getItemAtPostion(pos)
                        cartDataSource!!.deleteCart(deleteItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object:SingleObserver<Int>{
                                override fun onSuccess(t: Int) {
                                    adapter!!.notifyItemRemoved(pos)
                                    sumCart()
                                    EventBus.getDefault().postSticky(CounterCartEvent(true))
                                    Toast.makeText(context,"Item Deleted",Toast.LENGTH_SHORT).show()
                                }

                                override fun onSubscribe(d: Disposable) {
                                }

                                override fun onError(e: Throwable) {
                                   Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
                                }

                            })
                    }

                }))
            }

        }
        txt_total_price = root!!.findViewById(R.id.txt_total_price) as TextView
        txt_empty_cart = root!!.findViewById(R.id.txt_empty_cart) as TextView
        groupPlace_holder = root!!.findViewById(R.id.group_place_holder) as CardView
    }

    private fun sumCart() {
        cartDataSource!!.sumPrice(`in`.ecommerce.takeaway.Common.Common.current_user!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Double>{
                override fun onSuccess(t: Double) {
                   txt_total_price!!.text= StringBuilder("Total: ").append(t)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                   if(e.message!!.contains("Query returned empty"))
                       Toast.makeText(context,""+e.message!!,Toast.LENGTH_LONG).show()
                }

            })
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        cartViewModel.onStop()
        compositeDisposable.clear()
        EventBus.getDefault().postSticky(HideCartFab(false))
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onUpdateItemCart(event: UpdateItemsInCart){
        if(event.cartitem !=null){
            recyclerViewState = recyclerCart!!.layoutManager!!.onSaveInstanceState()
            cartDataSource!!.updateCart(event.cartitem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Int>{
                    override fun onSuccess(t: Int) {
                        calculateTotalPrice()
                        recyclerCart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context,"[UPDATE CART"+e.message,Toast.LENGTH_LONG).show()
                    }

                })
        }
    }

    private fun calculateTotalPrice() {
        cartDataSource!!.sumPrice(`in`.ecommerce.takeaway.Common.Common.current_user!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Double>{
                override fun onSuccess(price: Double) {
                    txt_total_price!!.text = StringBuilder("Total: ").append(`in`.ecommerce.takeaway.Common.Common.FORMAT_PRICE(price))
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    if(e.message!!.contains("Query returned empty"))
                         Toast.makeText(context,"[SUM CART"+e.message,Toast.LENGTH_LONG).show()
                }

            })
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu!!.findItem(R.id.action_settings).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.cart_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_clear_cart){

            cartDataSource!!.cleanCart(`in`.ecommerce.takeaway.Common.Common.current_user!!.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :SingleObserver<Int>{
                    override fun onSuccess(t: Int) {
                        Toast.makeText(context,"Cart Cleared",Toast.LENGTH_LONG).show()
                        EventBus.getDefault().postSticky(CounterCartEvent(true))
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context,""+e.message,Toast.LENGTH_LONG).show()
                    }

                })
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}