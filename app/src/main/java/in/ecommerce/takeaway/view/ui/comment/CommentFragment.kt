package `in`.ecommerce.takeaway.view.ui.comment

import `in`.ecommerce.takeaway.Adapter.MyCommentAdapter
import `in`.ecommerce.takeaway.Callback.ICommentCallback
import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Model.CommentModel
import `in`.ecommerce.takeaway.R
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog

class CommentFragment : BottomSheetDialogFragment(),ICommentCallback{

    private var recycler_comment: RecyclerView?=null
    private var commentViewModel:CommentViewModel?=null
    private var listener:ICommentCallback
    private var dialog: AlertDialog?=null

    init {
        listener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val itemView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_comment_fragment,container,false)

        initView(itemView)
        loadCommentFromFireBase()

        commentViewModel!!.mutableLiveDataCommentList!!.observe(this, Observer {
            val adapter = MyCommentAdapter(context!!,it)
            recycler_comment!!.adapter = adapter
        })
        return itemView
    }

    private fun loadCommentFromFireBase() {
        dialog!!.show()

        val commentModels = ArrayList<CommentModel>()
        FirebaseDatabase.getInstance().getReference(Common.COMMENT_REF)
            .child(Common.FOOD_SELECTED!!.id!!)
            .orderByChild("commentTimeStamp")
            .limitToLast(100)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    listener.onCommentListFailed(p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                   for (commentSnapShot in p0.children){
                       val commentModel = commentSnapShot.getValue(CommentModel::class.java)
                       commentModels.add(commentModel!!)
                   }
                    listener.onCommentListSuccess(commentModels)
                }

            })
    }

    private fun initView(itemView: View?) {

        commentViewModel = ViewModelProviders.of(this).get(CommentViewModel::class.java)
        dialog =  SpotsDialog.Builder().setContext(context!!).setCancelable(false).build()
        recycler_comment = itemView!!.findViewById(R.id.recycler_comment) as RecyclerView
        recycler_comment!!.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,true)
        recycler_comment!!.layoutManager = layoutManager
        recycler_comment!!.addItemDecoration(DividerItemDecoration(context!!,layoutManager.orientation))

    }

    override fun onCommentListSuccess(commentModel: List<CommentModel>) {
        dialog!!.dismiss()
        commentViewModel!!.setCommentList(commentModel)
    }

    override fun onCommentListFailed(message: String) {
        dialog!!.dismiss()
    }

    companion object{
        private var instance:CommentFragment?=null

        fun getInstance():CommentFragment{
            if (instance==null){
               instance = CommentFragment()
            }
            return instance!!
        }
    }
}