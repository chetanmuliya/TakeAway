package `in`.ecommerce.takeaway.view.ui.fooddetail

import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Model.CommentModel
import `in`.ecommerce.takeaway.Model.FoodModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FoodDetailViewModel : ViewModel() {

   private var mutableLiveDataFood:MutableLiveData<FoodModel>?=null
   private var mutableLiveDataComment:MutableLiveData<CommentModel>?=null

    init {
        mutableLiveDataComment = MutableLiveData()
    }

    fun getMutableLiveDataFood():MutableLiveData<FoodModel>{
        if (mutableLiveDataFood==null)
            mutableLiveDataFood= MutableLiveData()
            mutableLiveDataFood!!.value= Common.FOOD_SELECTED

        return mutableLiveDataFood!!
    }

    fun getMutableLiveDataComment():MutableLiveData<CommentModel>{
        if (mutableLiveDataComment==null)
            mutableLiveDataComment= MutableLiveData()

        return mutableLiveDataComment!!
    }
    fun setCommentModel(commentModel: CommentModel) {
       if (mutableLiveDataComment!=null)
           mutableLiveDataComment!!.value = (commentModel)
    }

    fun setFoodmodel(foodModel: FoodModel) {
        if (mutableLiveDataFood!=null)
            mutableLiveDataFood!!.value = (foodModel)
    }
}