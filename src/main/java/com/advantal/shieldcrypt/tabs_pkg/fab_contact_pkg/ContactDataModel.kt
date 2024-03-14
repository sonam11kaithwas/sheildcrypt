package com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//import kotlinx.parcelize.Parcelize
/**
 * Created by Sonam on 28-09-2022 18:42.
 */
//@Entity
//    (tableName = "tb_contact")
//class ContactDataModel {
//    @PrimaryKey(autoGenerate = true)
////    @ColumnInfo(name = "id")
////    @NotNull
//    var id: Int = 0
//    var image:String=""
//    @ColumnInfo(name = "contactName")
//    var contactName:String=""
//    var mobileNumber:String=""
//
//}

//@Parcelize
//@SerializedName
@Entity(tableName = "user_table") // User Entity represents a table within the database.
data class ContactDataModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int, // <- 'id' is the primary key which will be autogenerated by the Room library.
    val contactName: String,
    val username: String,
    val mobileNumber: String,
    val image: String,
    val createdBy: String,
    val blocked: Boolean
//    ,val individualstatus: String
) : Serializable
//: Parcelable


