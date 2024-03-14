package com.advantal.shieldcrypt.my_database_pkg.all_dao_pkg

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.advantal.shieldcrypt.my_database_pkg.db_table.GroupDataModel

/**
 * Created by Sonam on 29-09-2022 13:06.
 */
@Dao
interface GroupDao {

    @Insert
    fun insertGroup(groupDataModel: GroupDataModel)

    //    @Query("select groupName from tb_group where groupJid=:groupJid")
    @Query("select * from tb_group where groupJid=:groupJid")
    fun getGrpName(groupJid: String): GroupDataModel

  @Query("Select groupName fROM tb_group where groupJid = :groupJid")
  fun getGroupName(groupJid: String):String
//    String getGroupName(String groupJid);

    @Query("delete from tb_group")
    fun delete()
}