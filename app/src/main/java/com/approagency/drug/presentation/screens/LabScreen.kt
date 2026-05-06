package com.approagency.drug.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.approagency.drug.domain.model.TestGroup
import com.approagency.drug.presentation.common.CustomBox
import com.approagency.drug.presentation.common.CustomModalDialog
import com.approagency.drug.presentation.common.Loading
import com.approagency.drug.presentation.common.PrimaryButton
import com.approagency.drug.presentation.viewModel.LabViewModel
import com.vada.caller.ui.theme.dime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import org.koin.androidx.compose.koinViewModel

@Composable
fun LabScreen(
    modifier: Modifier = Modifier,
    viewModel: LabViewModel = koinViewModel()
) {
    val testGroupsState by viewModel.testGroups.collectAsState()
    val isLoading = testGroupsState.isLoading
    val error = testGroupsState.error
    val testGroups = testGroupsState.testGroup

    Column(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CustomModalDialog(
                onDismissRequest = { /* maybe disable dismiss while loading */ },
                content = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Loading(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(MaterialTheme.dime.sm))
                        Text("در حال جستجو..." , textAlign = TextAlign.Right)
                    }
                },
//                        modifier = Modifier
//                            .matchParentSize()
//                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            )
        }
        else if(error != null){
            CustomBox(
                child = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(MaterialTheme.dime.md)
                    ) {
                        Text(
                            text = "خطا در دریافت اطلاعات",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        PrimaryButton(
                            text = "تلاش مجدد",
                            height = 40,
                            isLoading = false,
                            onClick = { viewModel.loadTestGroups() }
                        )
                    }
                }
            )
        }
        else if (testGroups != null){
            TestGroupsList(
                testGroupsFlow = testGroups,
                viewModel = viewModel
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("هیچ داده‌ای موجود نیست")
            }
        }

    }
}

@Composable
fun TestGroupsList(
    testGroupsFlow: Flow<List<TestGroup>>,
    viewModel: LabViewModel
) {
    val testGroups by testGroupsFlow.collectAsState(initial = emptyList())

    if (testGroups.isEmpty()) {
        CustomModalDialog(
            onDismissRequest = { /* maybe disable dismiss while loading */ },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Loading(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(MaterialTheme.dime.sm))
                    Text("در حال جستجو..." , textAlign = TextAlign.Right)
                }
            },
//                        modifier = Modifier
//                            .matchParentSize()
//                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(testGroups) { group ->
                TestGroupItem(
                    group = group,
                    onClick = {
                        // Navigate to test items of this group
//                        viewModel.loadTestItemsByGroup(group.id)
                    }
                )
            }
        }
    }
}

@Composable
fun TestGroupItem(
    group: TestGroup,
    onClick: () -> Unit
) {
    CustomBox (
        modifier = Modifier
            .padding(bottom = MaterialTheme.dime.sm)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = group.fname ?: group.ename ?: "بدون نام",
                style = MaterialTheme.typography.titleMedium.copy(
                    textAlign = TextAlign.Right,
                    textDirection = TextDirection.Rtl
                ),
                modifier = Modifier.padding(vertical = MaterialTheme.dime.xs)
            )
            group.detail?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(
                        textAlign = TextAlign.Right ,
                        textDirection = TextDirection.Rtl
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = MaterialTheme.dime.lg)
                )
            }
        }
    }
}