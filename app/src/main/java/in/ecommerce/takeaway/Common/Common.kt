package `in`.ecommerce.takeaway.Common

import `in`.ecommerce.takeaway.Model.CategoryModel
import `in`.ecommerce.takeaway.Model.FoodModel
import `in`.ecommerce.takeaway.Model.UserModel

object Common {
    var FOOD_SELECTED: FoodModel?=null
    var CATEGORY_SELECTED: CategoryModel?=null
    val CATEGORY_REF: String="Category"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0
    val BEST_DEALS_REF: String="BestDeals"
    val POPULAR_REF: String="MostPopular"
    var USER_REFERENCE="Users"
    var current_user:UserModel?=null
}