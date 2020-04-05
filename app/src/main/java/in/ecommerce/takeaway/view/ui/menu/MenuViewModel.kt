package `in`.ecommerce.takeaway.view.ui.menu

import `in`.ecommerce.takeaway.Callback.ICategoryCallback
import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Model.CategoryModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuViewModel : ViewModel(), ICategoryCallback {

   private var categoriesListMutableLiveData:MutableLiveData<List<CategoryModel>>?=null
    private var messageError:MutableLiveData<String> = MutableLiveData()
    private val categoryCallBackListener:ICategoryCallback

    init {
        categoryCallBackListener = this
    }

    override fun onCategoryLoadSuccess(categoryList: List<CategoryModel>) {
        categoriesListMutableLiveData!!.value = categoryList
    }

    override fun onCategoryLoadFailed(message: String) {
        messageError.value = message
    }

    val getCategoryList:MutableLiveData<List<CategoryModel>>
    get() {
        if (categoriesListMutableLiveData==null){
            categoriesListMutableLiveData = MutableLiveData()
            loadCategory()
        }
        return categoriesListMutableLiveData!!
    }

     fun getErrorMessage():MutableLiveData<String>{
        return messageError;
    }
    private fun loadCategory() {
        var tempList = ArrayList<CategoryModel>()
        var popularRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
        popularRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                categoryCallBackListener.onCategoryLoadFailed(p0.message!!)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (itemSnapShot in p0!!.children){
                    val model = itemSnapShot.getValue<CategoryModel>(CategoryModel::class.java)
                    model!!.menu_id = itemSnapShot.key
                    tempList.add(model!!)
                }
                categoryCallBackListener.onCategoryLoadSuccess(tempList)
            }

        })
    }

}