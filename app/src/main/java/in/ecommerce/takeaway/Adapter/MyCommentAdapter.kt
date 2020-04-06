package `in`.ecommerce.takeaway.Adapter

import `in`.ecommerce.takeaway.Model.CommentModel
import `in`.ecommerce.takeaway.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_comment_item.view.*

class MyCommentAdapter(internal var context:Context,internal var commentList:List<CommentModel>):RecyclerView.Adapter<MyCommentAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyCommentAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_comment_item,parent,false))
    }

    override fun getItemCount(): Int {
       return commentList.size
    }

    override fun onBindViewHolder(holder: MyCommentAdapter.MyViewHolder, position: Int) {
        holder.txt_comment_name!!.setText(commentList.get(position).name)
        //val timestamp  = commentList.get(position).date!!["timeStamp"]!!.toString().toLong()
    }

    class MyViewHolder(itemview:View):RecyclerView.ViewHolder(itemview) {

        var txt_comment_name:TextView?=null
        var txt_comment_date:TextView?=null
        var txt_comment:TextView?=null
        var ratingBar:RatingBar?=null

        init {
            txt_comment_name = itemview.findViewById(R.id.txt_comment_name) as TextView
            txt_comment_date = itemview.findViewById(R.id.txt_comment_date) as TextView
            txt_comment = itemview.findViewById(R.id.txt_comment) as TextView
            ratingBar = itemview.findViewById(R.id.rating_bar) as RatingBar
        }
    }
}