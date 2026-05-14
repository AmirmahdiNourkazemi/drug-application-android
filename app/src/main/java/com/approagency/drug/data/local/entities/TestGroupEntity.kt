package com.approagency.drug.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "testGroup")
data class TestGroupEntity(
    @PrimaryKey
    @ColumnInfo(name = "Id")
    val id: Int,

    @ColumnInfo(name = "Ename")
    val ename: String?,

    @ColumnInfo(name = "Fname")
    val fname: String?,

    @ColumnInfo(name = "Detail")
    val detail: String?,

    @ColumnInfo(name = "Isparent")
    val isParent: String?
)