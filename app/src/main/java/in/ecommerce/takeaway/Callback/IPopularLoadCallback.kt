package `in`.ecommerce.takeaway.Callback

import `in`.ecommerce.takeaway.Model.PopularCategoriesModel

interface IPopularLoadCallback {
    fun onPopularLoadSuccess(popularModelList:List<PopularCategoriesModel>)
    fun onPopularLoadFailed(message:String)
}