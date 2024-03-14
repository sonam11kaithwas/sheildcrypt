package com.advantal.shieldcrypt.ui.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import database.my_database_pkg.all_converters_pkg.GroupUsersConverters
import java.io.Serializable

data class CreateGrpListResponse(
    val response: List<ResponseItem?>? = null
) : java.io.Serializable

data class Designation(
    val code: String? = null,
    val updationDate: String? = null,
    val name: String? = null,
    val active: Boolean? = null,
    val id: Int? = null,
    val creationDate: String? = null
) : java.io.Serializable

data class Location(
    val code: String? = null,
    val updationDate: Any? = null,
    val name: String? = null,
    val active: Boolean? = null,
    val id: Int? = null,
    val creationDate: String? = null
) : java.io.Serializable

data class Unit(
    val code: String? = null,
    val updationDate: String? = null,
    val name: String? = null,
    val active: Boolean? = null,
    val id: Int? = null,
    val creationDate: String? = null
) : java.io.Serializable

@Entity(tableName = "group_table")
data class ResponseItem(
    val groupName: String? = null,
    val deleted: Boolean? = null,
    val groupDescription: String? = null,
    val updationDate: String? = null,
    val active: Boolean? = null,
    @PrimaryKey val id: Int? = null,
    val groupImage: String? = null,
    val creationDate: String? = null,
    val groupJid: String? = null,
    @TypeConverters(GroupUsersConverters::class) var users: List<UsersItem?>? = null
): Serializable

data class UsersItem(
    val googleId: String? = null,
    val lastName: String? = null,
    val callRecordingStatus: Boolean? = null,
    val passwordStatus: Int? = null,
    val mobileNumber: String? = null,
    val callForwardingStatus: Boolean? = null,
    val admin: Boolean? = null,
    val emailId: String? = null,
    val profilePhoto: String? = null,
    val blocked: Boolean? = null,
    val updationDate: Long? = null,
    val id: Int? = null,
    val verificationStatus: Boolean? = null,
    val otpEnable: Boolean? = null,
    val facebookId: String? = null,
    val active: Boolean? = null,
    val otp: String? = null,
    val creationDate: Long? = null,
    val virtualNumber: String? = null,
    val roomName: Any? = null,
    val firstName: String? = null,
    val dndStatus: Boolean? = null,
    val unit: Unit? = null,
    val deleted: Boolean? = null,
    val location: Location? = null,
    val designation: Designation? = null,
    val username: String? = null
): Serializable

