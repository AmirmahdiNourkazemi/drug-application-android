package com.approagency.pharmacy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.approagency.pharmacy.presentation.common.ErrorState
import com.approagency.pharmacy.presentation.common.Loading
import com.approagency.pharmacy.presentation.components.drugdetail.BrandNamesTabContent
import com.approagency.pharmacy.presentation.components.drugdetail.DosageFormsTabContent
import com.approagency.pharmacy.presentation.components.drugdetail.GeneralInfoTabContent
import com.approagency.pharmacy.presentation.components.drugdetail.ManufacturerInfoCard
import com.approagency.pharmacy.presentation.components.drugdetail.SpecializedInfoTabContent
import com.approagency.pharmacy.presentation.viewModel.DrugDetailViewModel
import com.approagency.pharmacy.presentation.viewModel.DrugDetailYabState
import com.vada.caller.ui.theme.LocalDime
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugDetailScreen(
    navController: NavController,
    detailUrl: String,
    viewModel: DrugDetailViewModel = koinViewModel()
) {
    val dime = LocalDime.current
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("عمومی", "تخصصی", "اشکال دارویی", "اسامی تجاری")

    LaunchedEffect(detailUrl) {
        viewModel.loadDrugDetail(detailUrl)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        when (val state = detailState) {
                            is DrugDetailYabState.Success -> {
                                Column {
                                    Text(
                                        text = state.drugDetail.persianName,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (state.drugDetail.englishName.isNotEmpty()) {
                                        Text(
                                            text = state.drugDetail.englishName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            else -> {
                                Text(
                                    text = "جزئیات دارو",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = detailState) {
                    DrugDetailYabState.Idle -> {}

                    DrugDetailYabState.Loading -> {
                        Loading(
                            modifier = Modifier.fillMaxSize(),
                            message = "در حال دریافت اطلاعات دارو..."
                        )
                    }

                    is DrugDetailYabState.Success -> {
                        val drugDetail = state.drugDetail

                        Column(modifier = Modifier.fillMaxSize()) {
                            // کارت سازنده (برای صفحات برند)
                            if (!drugDetail.isGeneric && drugDetail.manufacturer != null) {
                                ManufacturerInfoCard(
                                    manufacturer = drugDetail.manufacturer!!,
                                    genericInfo = drugDetail.genericInfo,
                                    modifier = Modifier.padding(
                                        horizontal = dime.md,
                                        vertical = dime.sm
                                    )
                                )
                            }

                            TabRow(
                                selectedTabIndex = selectedTab,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                tabs.forEachIndexed { index, title ->
                                    Tab(
                                        selected = selectedTab == index,
                                        onClick = { selectedTab = index },
                                        text = {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1
                                            )
                                        }
                                    )
                                }
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = dime.md),
                                contentPadding = PaddingValues(top = dime.md, bottom = dime.xxl),
                                verticalArrangement = Arrangement.spacedBy(dime.md)
                            ) {
                                when (selectedTab) {
                                    0 -> item { GeneralInfoTabContent(drugDetail) }
                                    1 -> item { SpecializedInfoTabContent(drugDetail) }
                                    2 -> item { DosageFormsTabContent(drugDetail) }
                                    3 -> item { BrandNamesTabContent(drugDetail) }
                                }
                            }
                        }
                    }

                    is DrugDetailYabState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorState(
                                message = state.message,
                                onRetry = { viewModel.loadDrugDetail(detailUrl) }
                            )
                        }
                    }
                }
            }
        }
    }
}
