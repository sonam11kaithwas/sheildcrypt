package database.my_database_pkg.all_dao_pkg

import androidx.room.*
import com.advantal.shieldcrypt.ui.model.ResponseItem

@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addGroupMember(user: ResponseItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAllGroupMember(user: List<ResponseItem>)

    @Query("SELECT groupName FROM group_table WHERE groupJid = :id")
    fun getGrpNmById(id: String): String

    @Query("SELECT * FROM group_table WHERE groupJid = :id")
    fun getGrpdetailsById(id: String): ResponseItem

    @Query("SELECT groupImage FROM group_table WHERE groupName = :id")
    fun getUserProfilePick(id: String): String

    @Query("delete from group_table")
    fun delete()

    @Query("delete from group_table where groupJid=:groupJid")
    fun deleteGrpById(groupJid:Int)

    @Query("SELECT * FROM group_table")
    fun getAllGrpList(): List<ResponseItem>


    @Query("SELECT groupName FROM group_table WHERE groupName = :mobileNumber")
    fun getUserNameByPhone(mobileNumber: String): String

    @Delete
    fun deleteModelById(model: ResponseItem?)

    @Update
    fun updateResponseItem(model: ResponseItem?)


    @Query("SELECT * from  group_table WHERE id = :id")
    fun getGroupDetailsById(id: String): ResponseItem
}