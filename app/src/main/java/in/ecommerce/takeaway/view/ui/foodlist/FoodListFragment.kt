package `in`.ecommerce.takeaway.view.ui.foodlist

import `in`.ecommerce.takeaway.Adapter.MyFoodListAdapter
import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.R
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FoodListFragment : Fragment() {

    private lateinit var foodListViewModel: FoodListViewModel

    var recycler_foodlist:RecyclerView?=null
    var layoutAnimationController:LayoutAnimationController?=null
    var adapter:MyFoodListAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodListViewModel =
            ViewModelProviders.of(this).get(FoodListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_foodlist, container, false)

        initViews(root)
        foodListViewModel.getMutableListData().observe(this, Observer {
            adapter = MyFoodListAdapter(context!!,it)
            recycler_foodlist!!.adapter = adapter
            recycler_foodlist!!.layoutAnimation = layoutAnimationController
        })

        return root
    }

    private fun initViews(root: View?) {
        recycler_foodlist = root!!.findViewById(R.id.recycler_food_list) as RecyclerView
        recycler_foodlist!!.setHasFixedSize(true)
        recycler_foodlist!!.layoutManager = LinearLayoutManager(context)

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)


        (activity as AppCompatActivity).supportActionBar!!.title = Common.CATEGORY_SELECTED!!.name
    }
}