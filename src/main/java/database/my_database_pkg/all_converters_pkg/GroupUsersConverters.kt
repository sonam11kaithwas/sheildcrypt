package database.my_database_pkg.all_converters_pkg

import androidx.room.TypeConverter
import com.advantal.shieldcrypt.ui.model.UsersItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class GroupUsersConverters {
    @TypeConverter
    fun toTaxData(strdata: String?): List<UsersItem?>? {
        val listType = object : TypeToken<List<UsersItem?>?>() {}.type
        val data: List<UsersItem> = Gson().fromJson<List<UsersItem>>(strdata, listType)
        return if (strdata == null) null else data
    }

    @TypeConverter
    fun toStringData(data: List<UsersItem?>?): String? {
        return if (data == null) null else Gson().toJson(data)
    }

}