package `in`.ecommerce.takeaway.view.ui.fooddetail

import `in`.ecommerce.takeaway.Model.FoodModel
import `in`.ecommerce.takeaway.R
import android.media.Rating
import android.os.Bundle
import android.os.TestLooperManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.StringBuilder

class FoodDetailFragment : Fragment() {

    private lateinit var foodDetailViewModel: FoodDetailViewModel

    private var img_food:ImageView?=null
    private var btnCart:CounterFab?=null
    private var btnRating:FloatingActionButton?=null
    private var food_name:TextView?=null
    private var food_description:TextView?=null
    private var food_price:TextView?=null
    private var number_button:ElegantNumberButton?=null
    private var ratingbar:RatingBar?=null
    private var btnShowComment:Button?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodDetailViewModel =
            ViewModelProviders.of(this).get(FoodDetailViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_food_detail, container, false)

        initView(root)
        foodDetailViewModel.getMutableLiveDataFood().observe(this, Observer {
           displayInfo(it)
        })
        return root
    }

    private fun displayInfo(it: FoodModel?) {
        Glide.with(context!!).load(it!!.image).into(img_food!!)
        food_name!!.text = StringBuilder(it!!.name!!)
        food_description!!.text = StringBuilder(it!!.description!!)
        food_price!!.text = StringBuilder(it!!.price!!.toString())
    }

    private fun initView(root: View?) {
       btnCart = root!!.findViewById(R.id.btnCart) as CounterFab
       img_food = root!!.findViewById(R.id.img_Food) as ImageView
       btnRating = root!!.findViewById(R.id.btn_rating) as FloatingActionButton
       food_name = root!!.findViewById(R.id.food_name) as TextView
       food_description = root!!.findViewById(R.id.food_description) as TextView
       food_price = root!!.findViewById(R.id.food_price) as TextView
       number_button = root!!.findViewById(R.id.number_button) as ElegantNumberButton
       ratingbar = root!!.findViewById(R.id.ratingbar) as RatingBar
       btnShowComment = root!!.findViewById(R.id.btnShowComment) as Button
    }
}