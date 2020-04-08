package `in`.ecommerce.takeaway.view.ui.fooddetail

import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Model.CommentModel
import `in`.ecommerce.takeaway.Model.FoodModel
import `in`.ecommerce.takeaway.R
import `in`.ecommerce.takeaway.view.ui.comment.CommentFragment
import android.app.AlertDialog
import android.media.Rating
import android.os.Bundle
import android.os.TestLooperManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
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

    private var waitingDialog:AlertDialog?=null
    private var rdi_group_size:RadioGroup?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodDetailViewModel =
            ViewModelProviders.of(this).get(FoodDetailViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_food_detail, container, false)

        initView(root)
        //getting fooddetail
        foodDetailViewModel.getMutableLiveDataFood().observe(this, Observer {
           displayInfo(it)
        })
        //getting food rating
        foodDetailViewModel.getMutableLiveDataComment().observe(this, Observer {
            submitRatingToFirebase(it)
        })
        return root
    }

    private fun submitRatingToFirebase(commentModel: CommentModel) {
        waitingDialog!!.show()
        FirebaseDatabase.getInstance().getReference(Common.COMMENT_REF)
            .child(Common.FOOD_SELECTED!!.id!!)
            .push()
            .setValue(commentModel)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){
                    addRatingToFood(commentModel.ratingValue.toDouble())
                }
                waitingDialog!!.dismiss()
            }
    }

    private fun addRatingToFood(ratingValue: Double) {
       FirebaseDatabase.getInstance()
           .getReference(Common.CATEGORY_REF)
           .child(Common.CATEGORY_SELECTED!!.menu_id!!)
           .child("foods")
           .child(Common.FOOD_SELECTED!!.key!!)
           .addListenerForSingleValueEvent(object : ValueEventListener{
               override fun onCancelled(p0: DatabaseError) {
                   waitingDialog!!.dismiss()
                   Toast.makeText(context,""+p0.message,Toast.LENGTH_LONG).show()
               }

               override fun onDataChange(dataSnapshot: DataSnapshot) {
                  if (dataSnapshot.exists()){
                      val foodModel = dataSnapshot.getValue(FoodModel::class.java)
                      foodModel!!.key = Common.FOOD_SELECTED!!.key
                      //appply rating
                      val sumRating = foodModel.ratingValue!!.toDouble() + (ratingValue)
                      val ratingCount = foodModel.ratingCount+1
                      val result = sumRating/ratingCount
                      val updateData = HashMap<String,Any>()
                      updateData["ratingValue"]=result
                      updateData["ratingCount"]=ratingCount

                      //update data in variable
                      foodModel.ratingCount = ratingCount
                      foodModel.ratingValue = result

                      dataSnapshot.ref
                          .updateChildren(updateData)
                          .addOnCompleteListener{task ->
                              waitingDialog!!.dismiss()
                              if (task.isSuccessful){
                                  Common.FOOD_SELECTED = foodModel
                                  foodDetailViewModel!!.setFoodmodel(foodModel)
                                  Toast.makeText(context,"Thank You!",Toast.LENGTH_LONG).show()
                              }
                          }
                  }
               }

           })
    }

    private fun displayInfo(it: FoodModel?) {
        Glide.with(context!!).load(it!!.image).into(img_food!!)
        food_name!!.text = StringBuilder(it!!.name!!)
        food_description!!.text = StringBuilder(it!!.description!!)
        food_price!!.text = StringBuilder(it!!.price!!.toString())

        ratingbar!!.rating = it!!.ratingValue.toFloat()

        //set size
        for(sizeModel in it!!.size){
            val radioButton = RadioButton(context)
            radioButton.setOnCheckedChangeListener{buttonView, b ->
                if (b)
                    Common.FOOD_SELECTED!!.userSelectedSize = sizeModel
                calculateTotalPrice()
            }
            val params = LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.MATCH_PARENT,1.0f)
            radioButton.layoutParams = params
            radioButton.text = sizeModel.name
            radioButton.tag = sizeModel.price

            rdi_group_size!!.addView(radioButton)

            //default radio button select
            if (rdi_group_size!!.childCount>0){
                val radioButton = rdi_group_size!!.getChildAt(0) as RadioButton
                radioButton.isChecked = true
            }
        }
    }

    private fun calculateTotalPrice() {
        var total_price = Common.FOOD_SELECTED!!.price.toDouble()
        var display_price= 0.0

        total_price += Common.FOOD_SELECTED!!.userSelectedSize!!.price!!.toDouble()

        display_price = total_price * number_button!!.number.toInt()
        display_price = Math.round(display_price*100.0)/100.0

        food_price!!.text = StringBuilder("").append(Common.FORMAT_PRICE(display_price)).toString()
    }

    private fun initView(root: View?) {
        waitingDialog = SpotsDialog.Builder().setContext(context!!).setCancelable(false).build()
       btnCart = root!!.findViewById(R.id.btnCart) as CounterFab
       img_food = root!!.findViewById(R.id.img_Food) as ImageView
       btnRating = root!!.findViewById(R.id.btn_rating) as FloatingActionButton
       food_name = root!!.findViewById(R.id.food_name) as TextView
       food_description = root!!.findViewById(R.id.food_description) as TextView
       food_price = root!!.findViewById(R.id.food_price) as TextView
       number_button = root!!.findViewById(R.id.number_button) as ElegantNumberButton
       ratingbar = root!!.findViewById(R.id.ratingbar) as RatingBar
       btnShowComment = root!!.findViewById(R.id.btnShowComment) as Button
       rdi_group_size = root!!.findViewById(R.id.rdi_group_size) as RadioGroup

        btnRating!!.setOnClickListener(View.OnClickListener {
            showDialogRating()
        })

        btnShowComment!!.setOnClickListener {
            val commentFragment = CommentFragment.getInstance()
            commentFragment.show(activity!!.supportFragmentManager,"CommentFragment")
        }
    }

    private fun showDialogRating() {
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle("Rating Food")
        builder.setMessage("Please Fill Information")

        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_rating_comment,null)

        val ratingBar = itemView.findViewById<RatingBar>(R.id.rating_bar)
        val edt_comment = itemView.findViewById<EditText>(R.id.edt_comment)

        builder.setView(itemView)
        builder.setNegativeButton("CANCEL"){dialog, which -> dialog.dismiss() }
        builder.setPositiveButton("OK"){dialog, which ->
            val commentModel = CommentModel()
            commentModel.name = Common.current_user!!.name
            commentModel.uid = Common.current_user!!.uid
            commentModel.comment = edt_comment.text.toString()
            commentModel.ratingValue = ratingBar.rating
            val serverTimeStamp = HashMap<String,Any>()
            serverTimeStamp["timeStamp"] = ServerValue.TIMESTAMP
            commentModel.commentTimeStamp = serverTimeStamp

            foodDetailViewModel!!.setCommentModel(commentModel)
        }
        val dialog = builder.create()
        dialog.show()
    }
}