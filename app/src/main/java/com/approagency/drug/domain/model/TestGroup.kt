package com.approagency.drug.domain.model

import com.approagency.drug.data.local.entities.TestGroupEntity

data class TestGroup(
    val id: Int,
    val ename: String?,
    val fname: String?,
    val detail: String?,
    val isParent: String?,
)


fun TestGroupEntity.toTestGroup(): TestGroup {
    return TestGroup(
        id = this.id,
        ename = this.ename,
        fname = this.fname,
        detail = this.detail,
        isParent = this.isParent
    )
}