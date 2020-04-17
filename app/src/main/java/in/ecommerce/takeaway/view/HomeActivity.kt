package `in`.ecommerce.takeaway.view

import `in`.ecommerce.takeaway.Common.Common
import `in`.ecommerce.takeaway.Database.CartDataSource
import `in`.ecommerce.takeaway.Database.CartDatabase
import `in`.ecommerce.takeaway.Database.LocalCartDataSource
import `in`.ecommerce.takeaway.EventBus.*
import `in`.ecommerce.takeaway.Model.CategoryModel
import `in`.ecommerce.takeaway.Model.FoodModel
import `in`.ecommerce.takeaway.Model.PopularCategoriesModel
import `in`.ecommerce.takeaway.R
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private var drawerLayout: DrawerLayout?=null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var cartDataSource: CartDataSource
    private var dialog: android.app.AlertDialog?=null

    override fun onResume() {
        super.onResume()
        countCartItem()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(this).cartDAO())

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_cart)
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_menu, R.id.nav_food_detail,
                R.id.nav_cart, R.id.nav_food_list
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var headerView = navView.getHeaderView(0)
        var txt_user = headerView.findViewById<TextView>(R.id.txt_user)
        Common.setSpanString("Hey, ",Common.current_user!!.name!!,txt_user)
        navView.setNavigationItemSelectedListener(object :NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(p0: MenuItem): Boolean {

                 p0.isChecked = true
                 drawerLayout!!.closeDrawers()
                if (p0.itemId == R.id.nav_signout){
                    signOut()
                }else  if (p0.itemId == R.id.nav_home){
                    navController.navigate(R.id.nav_home)
                }else  if (p0.itemId == R.id.nav_cart){
                    navController.navigate(R.id.nav_cart)
                }else  if (p0.itemId == R.id.nav_menu){
                    navController.navigate(R.id.nav_menu)
                }
                return true
            }

        })


        dialog =  SpotsDialog.Builder().setContext(this ).setCancelable(false).build()

        countCartItem()
    }

    private fun signOut() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign out")
            .setMessage("Do you really want to exit?")
            .setNegativeButton("CANCEL",{dialog, which -> dialog.dismiss() })
            .setNegativeButton("OK"){dialog, which ->

                Common.FOOD_SELECTED = null;
                Common.CATEGORY_SELECTED = null;
                Common.current_user = null;
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //event bus
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun categorySelected(event:CategoryClick){
        if(event.isSuccess){
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_list)
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick){
        if(event.isSuccess){
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_detail)
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onPopularFoodItemClick(e: PopularFoodItemClick){
        if(e.popularCategoriesModel != null){
            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                .child(e.popularCategoriesModel!!.menu_id!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    dialog!!.dismiss()
                    Toast.makeText(this@HomeActivity,""+p0.message,Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                   if (p0.exists()){
                        Common.CATEGORY_SELECTED = p0.getValue(CategoryModel::class.java)
                       FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                           .child(e.popularCategoriesModel!!.menu_id!!)
                           .child("foods")
                           .orderByChild("id")
                           .equalTo(e.popularCategoriesModel.food_id)
                           .limitToLast(1)
                           .addListenerForSingleValueEvent(object :ValueEventListener{
                               override fun onCancelled(p0: DatabaseError) {
                                   dialog!!.dismiss()
                                   Toast.makeText(this@HomeActivity,""+p0.message,Toast.LENGTH_LONG).show()
                               }

                               override fun onDataChange(p0: DataSnapshot) {
                                   if (p0.exists()){
                                        for (foodSnapShot in p0.children)
                                            Common.FOOD_SELECTED = foodSnapShot.getValue(FoodModel::class.java)
                                       findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_detail)
                                   }else{
                                       dialog!!.dismiss()
                                       Toast.makeText(this@HomeActivity,"Item doesn't exists",Toast.LENGTH_LONG).show()
                                   }
                               }

                           })
                   }else{
                       dialog!!.dismiss()
                       Toast.makeText(this@HomeActivity,"Item doesn't exists",Toast.LENGTH_LONG).show()
                   }
                }

            })
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onBestDealsFoodItemClick(e: BestDealItemClick){
        if(e.bestDealsModel != null){
            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                .child(e.bestDealsModel!!.menu_id!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        dialog!!.dismiss()
                        Toast.makeText(this@HomeActivity,""+p0.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()){
                            Common.CATEGORY_SELECTED = p0.getValue(CategoryModel::class.java)
                            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                                .child(e.bestDealsModel!!.menu_id!!)
                                .child("foods")
                                .orderByChild("id")
                                .equalTo(e.bestDealsModel!!.food_id)
                                .limitToLast(1)
                                .addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {
                                        dialog!!.dismiss()
                                        Toast.makeText(this@HomeActivity,""+p0.message,Toast.LENGTH_LONG).show()
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if (p0.exists()){
                                            for (foodSnapShot in p0.children)
                                                Common.FOOD_SELECTED = foodSnapShot.getValue(FoodModel::class.java)
                                            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_detail)
                                        }else{
                                            dialog!!.dismiss()
                                            Toast.makeText(this@HomeActivity,"Item doesn't exists",Toast.LENGTH_LONG).show()
                                        }
                                    }

                                })
                        }else{
                            dialog!!.dismiss()
                            Toast.makeText(this@HomeActivity,"Item doesn't exists",Toast.LENGTH_LONG).show()
                        }
                    }

                })
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onCountCartEvent(event: CounterCartEvent){
        if(event.isSuccess){
            countCartItem()
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun hideCartFab(event: HideCartFab){
        if(event.isHide){
            fab.hide()
        }else
        fab.show()
    }

    private fun countCartItem() {
      cartDataSource.countItemInCart(Common.current_user!!.uid!!)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object :SingleObserver<Int>{
              override fun onSuccess(t: Int) {
                  fab.count = t
              }

              override fun onSubscribe(d: Disposable) {
              }

              override fun onError(e: Throwable) {
                  if(e.message!!.contains("Query returned empty"))
                  Toast.makeText(this@HomeActivity,"[Count CART]"+e.message,Toast.LENGTH_LONG).show()
                  else
                      fab.count =0;
              }

          })
    }
}
