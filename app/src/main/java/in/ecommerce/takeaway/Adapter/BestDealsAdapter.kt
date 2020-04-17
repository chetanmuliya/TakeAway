package `in`.ecommerce.takeaway.Adapter

import `in`.ecommerce.takeaway.EventBus.BestDealItemClick
import `in`.ecommerce.takeaway.Model.BestDealsModel
import `in`.ecommerce.takeaway.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.bumptech.glide.Glide
import org.greenrobot.eventbus.EventBus

class BestDealsAdapter(context: Context,itemList:List<BestDealsModel>,isInfinite:Boolean)
    :LoopingPagerAdapter<BestDealsModel>(context,itemList,isInfinite){

    override fun inflateView(viewType: Int, container: ViewGroup?, listPosition: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_best_deal_items,container!!,false)
    }

    override fun bindView(convertView: View?, listPosition: Int, viewType: Int) {
        val imageview = convertView!!.findViewById<ImageView>(R.id.img_best_deal)
        val textview = convertView!!.findViewById<TextView>(R.id.txt_best_deal)

        Glide.with(context).load(itemList[listPosition].image).into(imageview)
        textview.text=itemList[listPosition].name

        convertView.setOnClickListener {
            EventBus.getDefault().postSticky(BestDealItemClick(itemList[listPosition]))
        }
    }
}