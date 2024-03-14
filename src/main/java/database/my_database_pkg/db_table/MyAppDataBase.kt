package database.my_database_pkg.db_table

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.advantal.shieldcrypt.auth_pkg.model.LoginResModelForDb
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel
import com.advantal.shieldcrypt.ui.model.ResponseItem
import database.my_database_pkg.all_converters_pkg.GroupUsersConverters
import database.my_database_pkg.all_dao_pkg.ContactDao
import database.my_database_pkg.all_dao_pkg.GroupDao
import database.my_database_pkg.all_dao_pkg.LoginResDao


/**
 * Created by Sonam-11 on 20/5/20.
 */
@Database(
    entities = [ContactDataModel::class, ResponseItem::class],//, LoginResModelForDb::class
    version = 1,
    exportSchema = false
)
@TypeConverters(GroupUsersConverters::class)
abstract class MyAppDataBase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun groupDao(): GroupDao
//    abstract fun loginResDao(): LoginResDao

    companion object {
        private var INSTANCE: MyAppDataBase? = null

        fun getUserDataBaseAppinstance(context: Context): MyAppDataBase? {
            if (INSTANCE == null) {
                synchronized(MyAppDataBase::class) {
                    Log.e("My test", "my test")
                    INSTANCE = Room.databaseBuilder(
                        context, MyAppDataBase::class.java, "userdatabase.db"
                    ).allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

    }
}

