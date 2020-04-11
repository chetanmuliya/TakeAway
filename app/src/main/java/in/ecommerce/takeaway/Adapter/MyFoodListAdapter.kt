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
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class MyFoodListAdapter(internal var context:Context,internal var foodmodels: List<FoodModel>)
    :RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder>(){

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
        var img_fav:ImageView?=null
        var img_cart:ImageView?=null
        internal var listener:IRecyclerItemClickListener?=null

        fun setListener(listener: IRecyclerItemClickListener){
            this.listener = listener
        }

        init {
           txt_food_name = itemview.findViewById(R.id.txt_food_name) as TextView
           txt_food_price = itemview.findViewById(R.id.txt_food_price) as TextView

           img_food_image = itemview.findViewById(R.id.img_food_image) as ImageView
           img_fav = itemview.findViewById(R.id.img_fav) as ImageView
           img_cart = itemview.findViewById(R.id.img_quick_cart) as ImageView
            itemview.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener!!.onItemClickListener(v!!,adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item,parent,false))
    }

    override fun getItemCount(): Int {
        return foodmodels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       Glide.with(context).load(foodmodels.get(position).image).into(holder.img_food_image!!)
        holder.txt_food_name!!.setText(foodmodels.get(position).name)
        holder.txt_food_price!!.setText(foodmodels.get(position).price.toString())

        //Event
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
                Common.FOOD_SELECTED = foodmodels.get(pos)
                Common.FOOD_SELECTED!!.key = pos.toString()
                EventBus.getDefault().postSticky(FoodItemClick(true,foodmodels.get(pos)))
            }

        })

        //on carticon clicked
        holder.img_cart!!.setOnClickListener{
            val cartItem = CartItem()
            cartItem.uid = Common.current_user!!.uid
            cartItem.userPhone = Common.current_user!!.phone

            cartItem.foodId = foodmodels.get(position).id!!
            cartItem.foodName = foodmodels.get(position).name!!
            cartItem.foodImage = foodmodels.get(position).image!!
            cartItem.foodPrice = foodmodels.get(position).price!!.toDouble()
            cartItem.foodQuantity =1
            cartItem.foodExtraPrice = 0.0
            cartItem.foodAddon = "Default"
            cartItem.foodSize = "Default"

            composiDisposable.add(carDataSources.insertOrReplaceAll(cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(context,"Add to Cart success",Toast.LENGTH_LONG).show()
                    //notify homeactivty
                    EventBus.getDefault().postSticky(CounterCartEvent(true))
                },{
                    Toast.makeText(context,"[INSER CART]"+it.message,Toast.LENGTH_LONG).show()
                }))
        }


    }
    fun onStop(){
        if(composiDisposable != null){
            composiDisposable.clear()
        }
    }
    /*override fun getItemViewType(position: Int): Int {
        return if(foodmodels.size==1)
            Common.DEFAULT_COLUMN_COUNT
        else{
            if (foodmodels.size%2==0)
                Common.DEFAULT_COLUMN_COUNT
            else
                if (position>1 && position == foodmodels.size-1)
                    Common.FULL_WIDTH_COLUMN
                else
                    Common.DEFAULT_COLUMN_COUNT
        }
    }*/
}