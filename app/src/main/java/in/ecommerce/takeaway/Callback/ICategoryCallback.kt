package `in`.ecommerce.takeaway.Callback

import `in`.ecommerce.takeaway.Model.CategoryModel
import `in`.ecommerce.takeaway.Model.PopularCategoriesModel

interface ICategoryCallback {
    fun onCategoryLoadSuccess(categoryList:List<CategoryModel>)
    fun onCategoryLoadFailed(message:String)
}