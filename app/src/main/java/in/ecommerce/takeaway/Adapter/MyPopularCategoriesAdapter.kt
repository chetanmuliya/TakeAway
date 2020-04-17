package `in`.ecommerce.takeaway.Adapter

import `in`.ecommerce.takeaway.Callback.IRecyclerItemClickListener
import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.EventBus.CategoryClick
import `in`.ecommerce.takeaway.EventBus.PopularFoodItemClick
import `in`.ecommerce.takeaway.Model.PopularCategoriesModel
import `in`.ecommerce.takeaway.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus

class MyPopularCategoriesAdapter(internal var context:Context,internal var popularCategoriesModels: List<PopularCategoriesModel>)
    :RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHolder>(){

    inner class MyViewHolder(itemview:View):RecyclerView.ViewHolder(itemview),
        View.OnClickListener  {


        var category_name:TextView?=null
        var category_image:CircleImageView?=null
        internal var listener: IRecyclerItemClickListener?=null

        fun setListener(listener: IRecyclerItemClickListener){
            this.listener = listener;
        }

        init {
           category_name = itemview.findViewById(R.id.txt_category_name)
           category_image = itemview.findViewById(R.id.category_image)
            itemview.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener!!.onItemClickListener(v!!,adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_popular_categories,parent,false))
    }

    override fun getItemCount(): Int {
        return popularCategoriesModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       Glide.with(context).load(popularCategoriesModels.get(position).image).into(holder.category_image!!)
        holder.category_name!!.setText(popularCategoriesModels.get(position).name)

        //Event
        holder.setListener(object :IRecyclerItemClickListener{
            override fun onItemClickListener(view: View, pos: Int) {
                //Common.CATEGORY_SELECTED = popularCategoriesModels.get(pos)
                EventBus.getDefault().postSticky(PopularFoodItemClick(popularCategoriesModels[pos]))
            }

        })
    }

}