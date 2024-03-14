package com.advantal.shieldcrypt.my_database_pkg.db_table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

/**
 * Created by Sonam on 29-09-2022 13:09.
 */
@Entity(tableName = "tb_group")
class GroupDataModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NotNull
    var id: Int = 0
    var groupImage: String = ""
    var groupName: String = ""
    var groupJid: String = ""
    var creationDate: String = ""
    var timestamp: String = ""
    var createdBy: String = ""


}