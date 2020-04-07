package `in`.ecommerce.takeaway.Callback

import `in`.ecommerce.takeaway.Model.BestDealsModel
import `in`.ecommerce.takeaway.Model.CommentModel
import `in`.ecommerce.takeaway.Model.PopularCategoriesModel

interface ICommentCallback {
    fun onCommentListSuccess(commentModel: List<CommentModel>)
    fun onCommentListFailed(message:String)
}