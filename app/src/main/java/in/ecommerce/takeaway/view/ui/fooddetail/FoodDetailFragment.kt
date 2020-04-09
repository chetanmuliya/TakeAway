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
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
import java.lang.StringBuilder

class FoodDetailFragment : Fragment(), TextWatcher {

    private lateinit var foodDetailViewModel: FoodDetailViewModel
    private lateinit var addonBottomSheetDialog: BottomSheetDialog

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

    //addon
    private var img_add_on:ImageView?=null
    private var chip_group_user_selected_Action:ChipGroup?=null
    private var chip_group_addon:ChipGroup?=null
    private var edt_search:EditText?=null

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

        //addon
        if(Common.FOOD_SELECTED!!.userSelectedAddon != null && Common.FOOD_SELECTED!!.userSelectedAddon!!.size>0) {
            for (addonModel in Common.FOOD_SELECTED!!.userSelectedAddon!!) {
                total_price+=addonModel.price!!.toDouble()
            }
        }
        //size
        total_price += Common.FOOD_SELECTED!!.userSelectedSize!!.price!!.toDouble()

        display_price = total_price * number_button!!.number.toInt()
        display_price = Math.round(display_price*100.0)/100.0

        food_price!!.text = StringBuilder("").append(Common.FORMAT_PRICE(display_price)).toString()
    }

    private fun initView(root: View?) {
        addonBottomSheetDialog = BottomSheetDialog(context!!,R.style.DailogStyle)
        var layout_user_selected_addon = layoutInflater.inflate(R.layout.layout_addon_display,null)
        chip_group_addon = layout_user_selected_addon.findViewById(R.id.layout_addon_display) as ChipGroup
        edt_search = layout_user_selected_addon.findViewById(R.id.edt_search) as EditText
        addonBottomSheetDialog.setContentView(layout_user_selected_addon)

        addonBottomSheetDialog.setOnDismissListener{dialog ->
            displayUserSelectedAddon()
            calculateTotalPrice()
        }


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
       img_add_on = root!!.findViewById(R.id.img_add_addon) as ImageView
       chip_group_user_selected_Action = root!!.findViewById(R.id.chip_group_user_selected_addon) as ChipGroup


        img_add_on!!.setOnClickListener(View.OnClickListener {
            if (Common.FOOD_SELECTED!!.addon!=null){
                displayAllAddOn()
                addonBottomSheetDialog.show()
            }
        })
        btnRating!!.setOnClickListener(View.OnClickListener {
            showDialogRating()
        })

        btnShowComment!!.setOnClickListener {
            val commentFragment = CommentFragment.getInstance()
            commentFragment.show(activity!!.supportFragmentManager,"CommentFragment")
        }
    }

    private fun displayAllAddOn() {
        if (Common.FOOD_SELECTED!!.addon.size>0){
            chip_group_addon!!.clearCheck()
            chip_group_addon!!.removeAllViews()

            edt_search!!.addTextChangedListener(this)

            for (addonModel in Common.FOOD_SELECTED!!.addon!!){

                    val chip = layoutInflater.inflate(R.layout.layout_chip,null,false) as Chip
                    chip.text = StringBuilder(addonModel.name!!).append("(+$").append(addonModel.price).append(")").toString()
                    //chip.isClickable = false
                    chip.setOnCheckedChangeListener { compoundButton, b ->
                        if (b){
                            if (Common.FOOD_SELECTED!!.userSelectedAddon==null)
                                Common.FOOD_SELECTED!!.userSelectedAddon = ArrayList()
                            Common.FOOD_SELECTED!!.userSelectedAddon!!.add(addonModel)
                        }
                    }
                    chip_group_addon!!.addView(chip)
            }

        }
    }

    private fun displayUserSelectedAddon() {
        if(Common.FOOD_SELECTED!!.userSelectedAddon != null && Common.FOOD_SELECTED!!.userSelectedAddon!!.size>0){
            chip_group_user_selected_Action!!.removeAllViews()
            for (addonModel in Common.FOOD_SELECTED!!.userSelectedAddon!!){
                val chip = layoutInflater.inflate(R.layout.layout_chip_with_delete,null,false) as Chip
                chip.text = StringBuilder(addonModel.name!!).append("(+$").append(addonModel.price).append(")").toString()
                chip.isClickable = false
                chip.setOnCloseIconClickListener { v ->
                    chip_group_user_selected_Action!!.removeView(v)
                    Common.FOOD_SELECTED!!.userSelectedAddon!!.remove(addonModel)
                    calculateTotalPrice()
                }
                chip_group_user_selected_Action!!.addView(chip)
            }
        }else if(Common.FOOD_SELECTED!!.userSelectedAddon!!.size == 0)
            chip_group_user_selected_Action!!.removeAllViews()
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

    override fun afterTextChanged(s: Editable?) {
        TODO("Not yet implemented")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        TODO("Not yet implemented")
    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
        chip_group_addon!!.clearCheck()
        chip_group_addon!!.removeAllViews()
        for (addonModel in Common.FOOD_SELECTED!!.addon!!){
            if (addonModel.name!!.toLowerCase().contains(charSequence.toString().toLowerCase())){
                val chip = layoutInflater.inflate(R.layout.layout_chip,null,false) as Chip
                chip.text = StringBuilder(addonModel.name!!).append("(+$").append(addonModel.price).append(")").toString()
                //chip.isClickable = false
                chip.setOnCheckedChangeListener { compoundButton, b ->
                    if (b){
                        if (Common.FOOD_SELECTED!!.userSelectedAddon==null)
                            Common.FOOD_SELECTED!!.userSelectedAddon = ArrayList()
                            Common.FOOD_SELECTED!!.userSelectedAddon!!.add(addonModel)
                    }
                }
                chip_group_addon!!.addView(chip)
            }
        }
    }
}