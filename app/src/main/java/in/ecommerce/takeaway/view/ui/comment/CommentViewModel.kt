package `in`.ecommerce.takeaway.view.ui.comment

import `in`.ecommerce.takeaway.Model.CommentModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommentViewModel : ViewModel() {

    var mutableLiveDataCommentList : MutableLiveData<List<CommentModel>>?=null

    init {
        mutableLiveDataCommentList = MutableLiveData()
    }

    fun setCommentList(commentList: List<CommentModel>){
        mutableLiveDataCommentList!!.value = commentList
    }
}