package `in`.ecommerce.takeaway.EventBus

import `in`.ecommerce.takeaway.Database.CartItem
import `in`.ecommerce.takeaway.Model.CategoryModel
import `in`.ecommerce.takeaway.Model.FoodModel
import io.reactivex.internal.operators.maybe.MaybeDoAfterSuccess

class UpdateItemsInCart(var cartitem:CartItem) {
}