package `in`.ecommerce.takeaway.EventBus

import `in`.ecommerce.takeaway.Model.CategoryModel
import `in`.ecommerce.takeaway.Model.FoodModel
import io.reactivex.internal.operators.maybe.MaybeDoAfterSuccess

class FoodItemClick(var isSuccess: Boolean,var category:FoodModel) {
}