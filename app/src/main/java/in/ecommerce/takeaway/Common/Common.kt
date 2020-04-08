package `in`.ecommerce.takeaway.Common

import `in`.ecommerce.takeaway.Model.CategoryModel
import `in`.ecommerce.takeaway.Model.FoodModel
import `in`.ecommerce.takeaway.Model.UserModel
import java.lang.StringBuilder
import java.math.RoundingMode
import java.text.DecimalFormat

object Common {
    fun FORMAT_PRICE(price: Double): String {
        if (price != 0.toDouble()){
            val df = DecimalFormat("#,##0.00")
            df.roundingMode = RoundingMode.HALF_UP
            val finalPrice = StringBuilder(df.format(price)).toString()
            return finalPrice.replace(".",",")
        }else
            return "0,00"
    }

    val COMMENT_REF: String ="Comments"
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