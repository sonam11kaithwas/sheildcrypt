package com.advantal.shieldcrypt.my_database_pkg.all_dao_pkg


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel

/**
 * Created by Sonam on 28-09-2022 18:38.
 */
@Dao
interface ContactDao {
    @Query("SELECT image FROM user_table WHERE mobileNumber = :mobileNumber")
    fun getUserProfilePick(mobileNumber: String): String
    @Query("SELECT id FROM user_table WHERE mobileNumber = :mobileNumber")
    fun getUserIdByPhone(mobileNumber: String): Int

    @Query("SELECT contactName FROM user_table WHERE mobileNumber = :mobileNumber")
    fun getUser(mobileNumber: String): String
    @Query("select * from user_table where mobileNumber!=:phoneNumber order by username Asc")
    fun getAllNotes(phoneNumber: String): MutableList<ContactDataModel>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(user: ContactDataModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAllUser(user: List<ContactDataModel>)

//    @Query("select * from user_table where mobileNumber!=:phoneNumber order by username Asc")
//    fun getAllNotes(phoneNumber: String): MutableList<ContactDataModel>?

    @Query("SELECT COUNT(*) from user_table")
    fun getTotleCount(): Int

    @Query("SELECT blocked FROM user_table WHERE mobileNumber = :usernamed")
    fun getIsBlockedUser(usernamed: String): Boolean

//    @Query("SELECT image FROM user_table WHERE mobileNumber = :mobileNumber")
//    fun getUser(mobileNumber: String): String


    @Query("SELECT * FROM user_table WHERE mobileNumber=:contactName")
    fun getContactNameById(contactName: String): ContactDataModel

    @Query("SELECT * FROM user_table WHERE mobileNumber=:contactName")
    fun getContactDataByThreadId(contactName: String): ContactDataModel

    @Query("SELECT * FROM user_table WHERE mobileNumber=:mobileNumber")
    fun getContactByThreadId(mobileNumber: String): ContactDataModel

//    @Update
//    fun updateContact(contactName: ContactDataModel)

    @Query("UPDATE user_table SET blocked=:blocked WHERE id = :id")
    fun update(blocked: Boolean?, id: Int)

    @Query("SELECT * FROM user_table WHERE id=:id")
    fun getDataModel(id: Int): ContactDataModel

    @Query("delete from user_table")
    fun delete()


}