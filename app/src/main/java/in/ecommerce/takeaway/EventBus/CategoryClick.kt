package `in`.ecommerce.takeaway.EventBus

import `in`.ecommerce.takeaway.Model.CategoryModel
import io.reactivex.internal.operators.maybe.MaybeDoAfterSuccess

class CategoryClick(var isSuccess: Boolean,var category:CategoryModel) {
}