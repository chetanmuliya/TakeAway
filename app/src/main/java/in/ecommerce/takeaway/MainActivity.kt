package `in`.ecommerce.takeaway

import `in`.ecommerce.takeaway.Remote.ICloudFunctions
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import dmax.dialog.SpotsDialog
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var listener:FirebaseAuth.AuthStateListener
    private lateinit var dialog:AlertDialog
    private val compositeDisposable = CompositeDisposable()
    private lateinit var cloudFunction:ICloudFunctions

    companion object{
        private val APP_REQUEST_CODE = 7171;
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(listener);
    }

    override fun onStop() {
        super.onStop()
        if (firebaseAuth!=null)
            firebaseAuth.removeAuthStateListener(listener)
        compositeDisposable.clear()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init();
    }

    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        val user = firebaseAuth.currentUser
        if (user!=null){
            //login
            Toast.makeText(this,"Already Login",Toast.LENGTH_LONG).show()
        }else{
            //not login
            phoneLogin();
        }
    }

    private fun phoneLogin() {
        //val intent = Intent(this,AccountKitActivity)
    }
}
