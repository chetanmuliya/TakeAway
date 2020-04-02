package `in`.ecommerce.takeaway.view.ui.home

import `in`.ecommerce.takeaway.Adapter.BestDealsAdapter
import `in`.ecommerce.takeaway.Adapter.MyPopularCategoriesAdapter
import `in`.ecommerce.takeaway.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asksira.loopingviewpager.LoopingViewPager
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    var recyclerView:RecyclerView?=null
    var viewPager:LoopingViewPager?=null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        initView(root)
        homeViewModel.popularList.observe(this, Observer {
            var listData = it;
            val adapter = MyPopularCategoriesAdapter(context!!,listData)
            recycler_popular.adapter=adapter
        })

        homeViewModel.bestDealsList.observe(this, Observer {
            var itemData=it;
            val adapter= BestDealsAdapter(context!!,itemData,false)
            viewPager!!.adapter = adapter
        })

        return root
    }

    private fun initView(root:View) {
        viewPager = root.findViewById(R.id.viewpager)as LoopingViewPager
        recyclerView = root.findViewById(R.id.recycler_popular) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
    }

    override fun onResume() {
        super.onResume()
        viewPager!!.pauseAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        viewPager!!.pauseAutoScroll()
    }

}