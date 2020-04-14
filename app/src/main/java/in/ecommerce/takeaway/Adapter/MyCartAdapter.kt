package `in`.ecommerce.takeaway.Adapter

import `in`.ecommerce.takeaway.Callback.IRecyclerItemClickListener
import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Database.CartDataSource
import `in`.ecommerce.takeaway.Database.CartDatabase
import `in`.ecommerce.takeaway.Database.CartItem
import `in`.ecommerce.takeaway.Database.LocalCartDataSource
import `in`.ecommerce.takeaway.EventBus.CategoryClick
import `in`.ecommerce.takeaway.EventBus.CounterCartEvent
import `in`.ecommerce.takeaway.EventBus.FoodItemClick
import `in`.ecommerce.takeaway.EventBus.UpdateItemsInCart
import `in`.ecommerce.takeaway.Model.FoodModel
import `in`.ecommerce.takeaway.Model.PopularCategoriesModel
import `in`.ecommerce.takeaway.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Scheduler
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class MyCartAdapter(internal var context:Context,internal var cartList: List<CartItem>)
    :RecyclerView.Adapter<MyCartAdapter.MyViewHolder>(){

    private val composiDisposable:CompositeDisposable
    private val carDataSources:CartDataSource

    init {
        composiDisposable = CompositeDisposable()
        carDataSources = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

    inner class MyViewHolder(itemview:View):RecyclerView.ViewHolder(itemview),
        View.OnClickListener {


        var txt_food_name:TextView?=null
        var txt_food_price:TextView?=null
        var img_food_image:ImageView?=null
        var number_button:ElegantNumberButton?=null

        internal var listener:IRecyclerItemClickListener?=null

        fun setListener(listener: IRecyclerItemClickListener){
            this.listener = listener
        }

        init {
           txt_food_name = itemview.findViewById(R.id.txt_food_name) as TextView
           txt_food_price = itemview.findViewById(R.id.txt_food_price) as TextView
           number_button = itemview.findViewById(R.id.number_button) as ElegantNumberButton

           img_food_image = itemview.findViewById(R.id.img_cart) as ImageView
            itemview.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener!!.onItemClickListener(v!!,adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false))
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       Glide.with(context).load(cartList[position].foodImage).into(holder.img_food_image!!)
        holder.txt_food_name!!.text = StringBuilder(cartList[position].foodName!!)
        holder.number_button!!.number = cartList[position].foodQuantity.toString()
        holder.txt_food_price!!.text = StringBuilder("").append(cartList[position].foodPrice!! + cartList[position].foodExtraPrice!!)

        //Event
        holder.number_button!!.setOnValueChangeListener{ view, oldValue, newValue ->
            cartList[position].foodQuantity = newValue
            EventBus.getDefault().postSticky(UpdateItemsInCart(cartList[position]))
        }
        //Event
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
               /* Common.FOOD_SELECTED = cartList.get(pos)
                Common.FOOD_SELECTED!!.key = pos.toString()
                EventBus.getDefault().postSticky(FoodItemClick(true,foodmodels.get(pos)))*/
            }

        })

    }
    fun onStop(){
        if(composiDisposable != null){
            composiDisposable.clear()
        }
    }

}