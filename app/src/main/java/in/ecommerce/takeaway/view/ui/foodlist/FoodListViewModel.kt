package `in`.ecommerce.takeaway.view.ui.foodlist

import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Model.FoodModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FoodListViewModel : ViewModel() {

    private var foodlistMutableLiveData:MutableLiveData<List<FoodModel>>?=null

    fun  getMutableListData():MutableLiveData<List<FoodModel>>{

        if (foodlistMutableLiveData==null){
            foodlistMutableLiveData = MutableLiveData()
            foodlistMutableLiveData!!.value = Common.CATEGORY_SELECTED!!.foods
        }
        return foodlistMutableLiveData!!
    }


}