package `in`.ecommerce.takeaway.Database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CartDAO {
    @Query("SELECT * FROM Cart WHERE uid=:uid")
    fun getAllCart(uid:String):Flowable<List<CartItem>>

    @Query("SELECT SUM(foodQuantity) FROM Cart WHERE uid=:uid")
    fun countItemInCart(uid:String):Single<Int>

    @Query("SELECT SUM((foodPrice+foodExtraPrice)*foodQuantity) FROM Cart WHERE uid=:uid")
    fun sumPrice(uid:String):Single<Double>

    @Query("SELECT * FROM Cart WHERE foodId=:foodid AND uid=:uid")
    fun getItemInCart(foodid:String,uid:String):Flowable<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceAll(vararg cartItem: CartItem):Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCart(cartItem: CartItem):Single<Int>

    @Delete
    fun deleteCart(cartItem: CartItem):Single<Int>

    @Query("DELETE FROM Cart WHERE uid=:uid")
    fun cleanCart(uid:String):Single<Int>

    @Query("SELECT * FROM Cart WHERE foodId=:foodid AND uid=:uid AND foodsize=:foodsize AND foodAddon=:foodAddon")
    fun getItemWithAllOptionsInCart(uid:String,foodid:String,foodsize:String,foodAddon:String):Single<CartItem>
}