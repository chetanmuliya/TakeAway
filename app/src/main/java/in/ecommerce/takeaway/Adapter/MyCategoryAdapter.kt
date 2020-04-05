package `in`.ecommerce.takeaway.Adapter

import `in`.ecommerce.takeaway.Callback.IRecyclerItemClickListener
import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.EventBus.CategoryClick
import `in`.ecommerce.takeaway.Model.CategoryModel
import `in`.ecommerce.takeaway.Model.PopularCategoriesModel
import `in`.ecommerce.takeaway.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus

class MyCategoryAdapter(internal var context:Context,internal var categoryList: List<CategoryModel>)
    :RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder>(){

    inner class MyViewHolder(itemview:View):RecyclerView.ViewHolder(itemview),
        View.OnClickListener {


        var category_name:TextView?=null
        var category_image:ImageView?=null
        internal var listener:IRecyclerItemClickListener?=null

        fun setListener(listener: IRecyclerItemClickListener){
            this.listener = listener;
        }
        init {
           category_name = itemview.findViewById(R.id.txt_category) as TextView
           category_image = itemview.findViewById(R.id.img_category) as ImageView
            itemview.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener!!.onItemClickListener(v!!,adapterPosition)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       Glide.with(context).load(categoryList.get(position).image).into(holder.category_image!!)
        holder.category_name!!.setText(categoryList.get(position).name)

        //Event
        holder.setListener(object :IRecyclerItemClickListener{
            override fun onItemClickListener(view: View, pos: Int) {
                Common.CATEGORY_SELECTED = categoryList.get(pos)
                EventBus.getDefault().postSticky(CategoryClick(true,categoryList.get(pos)))
            }

        })
    }

    override fun getItemViewType(position: Int): Int {
        return if(categoryList.size==1)
            Common.DEFAULT_COLUMN_COUNT
        else{
            if (categoryList.size%2==0)
                Common.DEFAULT_COLUMN_COUNT
            else
                if (position>1 && position == categoryList.size-1)
                    Common.FULL_WIDTH_COLUMN
                else
                    Common.DEFAULT_COLUMN_COUNT
        }
    }
}