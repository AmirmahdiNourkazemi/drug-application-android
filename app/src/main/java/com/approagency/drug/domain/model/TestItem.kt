package com.approagency.drug.domain.model

import com.approagency.drug.data.local.entities.TestItemEntity

data class TestItem(
    val id: Int,
    val groupId: Int,
    val title: String?,
    val normalValue: String?,
    val detail: String?
)
fun TestItemEntity.toTestItem(): TestItem {
    return TestItem(
        id = this.id,
        groupId = this.groupId,
        title = this.title,
        normalValue = this.normalValue,
        detail = this.detail
    )
}