package com.approagency.drug.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "testItem",
//    foreignKeys = [
//        ForeignKey(
//            entity = TestGroupEntity::class,
//            parentColumns = ["Id"],
//            childColumns = ["Group_Id"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
    indices = [Index(value = ["Group_Id"])]
)
data class TestItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "Id")
    val id: Int,

    @ColumnInfo(name = "Group_Id")
    val groupId: Int,

    @ColumnInfo(name = "Title")
    val title: String?,

    @ColumnInfo(name = "Normal_Value")
    val normalValue: String?,

    @ColumnInfo(name = "Detail")
    val detail: String?
)