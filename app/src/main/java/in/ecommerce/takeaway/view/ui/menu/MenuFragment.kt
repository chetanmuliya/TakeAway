package `in`.ecommerce.takeaway.view.ui.menu

import `in`.ecommerce.takeaway.Adapter.MyCategoryAdapter
import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Common.SpaceItemDecoration
import `in`.ecommerce.takeaway.R
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : Fragment() {

    private lateinit var menuViewModel: MenuViewModel
    private lateinit var dialog:AlertDialog
    private lateinit var layoutAnimamationController: LayoutAnimationController
    private var adapter:MyCategoryAdapter?=null
    private var recycler_menu:RecyclerView?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuViewModel =
            ViewModelProviders.of(this).get(MenuViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_menu, container, false)

        initViews(root)
        menuViewModel.getErrorMessage().observe(this, Observer {
            Toast.makeText(context,it,Toast.LENGTH_LONG).show()
        })

        menuViewModel.getCategoryList.observe(this, Observer {
            dialog.dismiss()
            adapter = MyCategoryAdapter(context!!,it)
            recycler_menu!!.adapter = adapter
            recycler_menu!!.layoutAnimation= layoutAnimamationController
        })

        return root
    }

    private fun initViews(root:View) {
        dialog=SpotsDialog.Builder().setContext(context)
            .setCancelable(false).build()
        dialog.show()
        layoutAnimamationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)
        recycler_menu = root.findViewById(R.id.recycler_menu) as RecyclerView
        recycler_menu!!.setHasFixedSize(true)
        val layoutmanager = GridLayoutManager(context,2)
        layoutmanager.orientation = RecyclerView.VERTICAL
        layoutmanager.spanSizeLookup = object :GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if(adapter!=null){
                    when(adapter!!.getItemViewType(position)){
                        Common.DEFAULT_COLUMN_COUNT-> 1
                        Common.FULL_WIDTH_COLUMN -> 2
                        else -> -1
                    }
                }else
                    -1
            }

        }
        recycler_menu!!.layoutManager = layoutmanager
        recycler_menu!!.addItemDecoration(SpaceItemDecoration(8))
    }
}