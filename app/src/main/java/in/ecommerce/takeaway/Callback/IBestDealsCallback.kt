package `in`.ecommerce.takeaway.Callback

import `in`.ecommerce.takeaway.Model.BestDealsModel
import `in`.ecommerce.takeaway.Model.PopularCategoriesModel

interface IBestDealsCallback {
    fun onBestDealsSuccess(bestDealsModel: List<BestDealsModel>)
    fun onBestDealsFailed(message:String)
}