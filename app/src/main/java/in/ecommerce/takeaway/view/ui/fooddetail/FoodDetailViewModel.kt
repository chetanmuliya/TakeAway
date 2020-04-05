package `in`.ecommerce.takeaway.view.ui.fooddetail

import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Model.FoodModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FoodDetailViewModel : ViewModel() {

   private var mutableLiveDataFood:MutableLiveData<FoodModel>?=null

    fun getMutableLiveDataFood():MutableLiveData<FoodModel>{
        if (mutableLiveDataFood==null)
            mutableLiveDataFood= MutableLiveData()
            mutableLiveDataFood!!.value= Common.FOOD_SELECTED

        return mutableLiveDataFood!!
    }
}