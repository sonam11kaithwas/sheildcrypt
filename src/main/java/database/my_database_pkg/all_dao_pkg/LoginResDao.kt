package database.my_database_pkg.all_dao_pkg

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.advantal.shieldcrypt.auth_pkg.model.LoginResModelForDb

@Dao
interface LoginResDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(user: LoginResModelForDb)

    @Query("delete from user_login_table")
    fun delete()

    @Query("SELECT * FROM user_login_table WHERE userid=:id ")
    fun getDataModel(id: String): LoginResModelForDb


    @Query("select * from user_login_table")
    fun getAllUsers(): MutableList<LoginResModelForDb>?

}