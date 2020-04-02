package `in`.ecommerce.takeaway.view.ui.home

import `in`.ecommerce.takeaway.Callback.IBestDealsCallback
import `in`.ecommerce.takeaway.Callback.IPopularLoadCallback
import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Model.BestDealsModel
import `in`.ecommerce.takeaway.Model.PopularCategoriesModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel(), IPopularLoadCallback, IBestDealsCallback {

    private var popularListMutableLiveData:MutableLiveData<List<PopularCategoriesModel>>?=null
    private var bestDealsListMutableLiveData:MutableLiveData<List<BestDealsModel>>?=null
    private lateinit var messageError:MutableLiveData<String>
    private var popularLoadCallback:IPopularLoadCallback
    private var bestDealsCallback:IBestDealsCallback

    val popularList:LiveData<List<PopularCategoriesModel>>
        get() {
           if (popularListMutableLiveData==null){
               popularListMutableLiveData = MutableLiveData()
               messageError= MutableLiveData()
               loadPopularList()
           }
            return popularListMutableLiveData!!
        }

    val bestDealsList:LiveData<List<BestDealsModel>>
        get() {
            if (bestDealsListMutableLiveData==null){
                bestDealsListMutableLiveData = MutableLiveData()
                messageError= MutableLiveData()
                loadbestDealsList()
            }
            return bestDealsListMutableLiveData!!
        }

    //interfaces
    init {
        popularLoadCallback = this
        bestDealsCallback = this
    }

    override fun onPopularLoadSuccess(popularModelList: List<PopularCategoriesModel>) {
        popularListMutableLiveData!!.value = popularModelList
    }

    override fun onPopularLoadFailed(message: String) {
       messageError.value = message
    }

    override fun onBestDealsSuccess(bestDealsModel: List<BestDealsModel>) {
        bestDealsListMutableLiveData!!.value=bestDealsModel
    }

    override fun onBestDealsFailed(message: String) {
        messageError.value = message
    }
    //
    private fun loadPopularList() {
        var tempList = ArrayList<PopularCategoriesModel>()
        var popularRef = FirebaseDatabase.getInstance().getReference(Common.POPULAR_REF)
        popularRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                popularLoadCallback.onPopularLoadFailed(p0.message!!)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (itemSnapShot in p0!!.children){
                    val model = itemSnapShot.getValue<PopularCategoriesModel>(PopularCategoriesModel::class.java)
                    tempList.add(model!!)
                }
                popularLoadCallback.onPopularLoadSuccess(tempList)
            }

        })
    }

    private fun loadbestDealsList() {
        var tempList = ArrayList<BestDealsModel>()
        var popularRef = FirebaseDatabase.getInstance().getReference(Common.BEST_DEALS_REF)
        popularRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                bestDealsCallback.onBestDealsFailed(p0.message!!)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (itemSnapShot in p0!!.children){
                    val model = itemSnapShot.getValue<BestDealsModel>(BestDealsModel::class.java)
                    tempList.add(model!!)
                }
                bestDealsCallback.onBestDealsSuccess(tempList)
            }

        })
    }

}