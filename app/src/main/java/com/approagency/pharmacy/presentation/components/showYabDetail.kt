package com.approagency.pharmacy.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.approagency.pharmacy.domain.model.DrugDetail
import com.approagency.pharmacy.presentation.common.CustomModalBottomSheet
import com.approagency.pharmacy.presentation.common.ErrorState
import com.approagency.pharmacy.presentation.common.Loading
import com.approagency.pharmacy.presentation.viewModel.DrugDetailViewModel
import com.approagency.pharmacy.presentation.viewModel.DrugDetailYabState
import com.vada.caller.ui.theme.LocalDime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugDetailBottomSheet(
    detailUrl: String?,
    onDismiss: () -> Unit,
    viewModel: DrugDetailViewModel
) {
    val dime = LocalDime.current
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()

    // Load detail when sheet opens with a valid URL
    LaunchedEffect(detailUrl) {
        if (detailUrl != null) {
            viewModel.loadDrugDetail(detailUrl)
        }
    }

    // Reset when sheet is closed
    LaunchedEffect(detailUrl) {
        if (detailUrl == null) {
            viewModel.reset()
        }
    }

    if (detailUrl != null) {
        CustomModalBottomSheet(
            onDismiss = {
                onDismiss()
                viewModel.reset()
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                when (val state = detailState) {
                    DrugDetailYabState.Idle -> {
                        // Initial state, nothing to show yet
                    }

                    DrugDetailYabState.Loading -> {
                        Loading(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(dime.xl),
                            message = "در حال دریافت اطلاعات دارو..."
                        )
                    }

                    is DrugDetailYabState.Success -> {
                        DrugDetailContent(
                            drugDetail = state.drugDetail,
                            modifier = Modifier.padding(bottom = dime.lg)
                        )
                    }

                    is DrugDetailYabState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(dime.xl),
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

@Composable
fun DrugDetailContent(
    drugDetail: DrugDetail,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("عمومی", "تخصصی", "اشکال دارویی", "اسامی تجاری")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dime.md)
    ) {
        // Header with close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dime.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = drugDetail.persianName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (drugDetail.englishName.isNotEmpty()) {
                    Text(
                        text = drugDetail.englishName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Categories
        if (drugDetail.drugClass != null || drugDetail.therapeuticClass != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dime.sm),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(dime.sm)
            ) {
                Column(
                    modifier = Modifier.padding(dime.sm)
                ) {
                    drugDetail.drugClass?.let {
                        Row {
                            Text(
                                text = "طبقه بندی: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = it,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    drugDetail.therapeuticClass?.let {
                        Spacer(modifier = Modifier.height(dime.xs))
                        Row {
                            Text(
                                text = "طبقه درمانی: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = it,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dime.sm)
        ) {
            tabs.forEachIndexed { index, title ->
                androidx.compose.material3.Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.weight(1f),
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        // Tab content
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(vertical = dime.md)
        ) {
            when (selectedTab) {
                0 -> {
                    // General Information
                    item {
                        GeneralInfoTab(drugDetail)
                    }
                }
                1 -> {
                    // Specialized Information
                    drugDetail.usage?.let {
                        item {
                            InfoSection(title = "موارد مصرف", content = it)
                        }
                    }
                    drugDetail.mechanism?.let {
                        item {
                            InfoSection(title = "مکانیسم اثر", content = it)
                        }
                    }
                    drugDetail.contraindications?.let {
                        item {
                            InfoSection(title = "موارد منع مصرف", content = it)
                        }
                    }
                    drugDetail.sideEffects?.let {
                        item {
                            InfoSection(title = "عوارض جانبی", content = it)
                        }
                    }
                    drugDetail.interactions?.let {
                        item {
                            InfoSection(title = "تداخلات دارویی", content = it)
                        }
                    }
                    drugDetail.warnings?.let {
                        item {
                            InfoSection(title = "هشدارها", content = it)
                        }
                    }
                    drugDetail.recommendations?.let {
                        item {
                            InfoSection(title = "توصیه‌های دارویی", content = it)
                        }
                    }
                }
                2 -> {
                    // Dosage Forms
                    if (drugDetail.dosageForms.isNotEmpty()) {
                        item {
                            DosageFormsSection(dosageForms = drugDetail.dosageForms)
                        }
                    }
                }
                3 -> {
                    // Brand Names
                    if (drugDetail.brandNames.isNotEmpty()) {
                        item {
                            BrandNamesSection(brandNames = drugDetail.brandNames)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneralInfoTab(drugDetail: DrugDetail) {
    val dime = LocalDime.current

    Column {
        // Pregnancy Info
        if (drugDetail.pregnancyCategory != null || drugDetail.pregnancyDescription != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dime.md),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Column(modifier = Modifier.padding(dime.md)) {
                    Text(
                        text = "مصرف در بارداری",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(dime.xs))
                    drugDetail.pregnancyCategory?.let {
                        Text(
                            text = "گروه: $it",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    drugDetail.pregnancyDescription?.let {
                        Spacer(modifier = Modifier.height(dime.xs))
                        Text(
                            text = it,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Usage
        drugDetail.usage?.let {
            InfoSection(title = "موارد مصرف", content = it)
        }

        // Contraindications
        drugDetail.contraindications?.let {
            InfoSection(title = "موارد منع مصرف", content = it)
        }

        // Side Effects
        drugDetail.sideEffects?.let {
            InfoSection(title = "عوارض جانبی", content = it)
        }

        // Warnings
        drugDetail.warnings?.let {
            InfoSection(title = "هشدارها", content = it)
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dime.md)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(dime.xs))
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(dime.xs))
        Text(
            text = content,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun DosageFormsSection(dosageForms: List<com.approagency.pharmacy.domain.model.DosageForm>) {
    val dime = LocalDime.current

    Column {
        Text(
            text = "اشکال دارویی",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = dime.sm)
        )

        dosageForms.forEach { form ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dime.sm),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(dime.sm)) {
                    Text(
                        text = form.persianName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = form.englishName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Row(
                        modifier = Modifier.padding(top = dime.xs),
                        horizontalArrangement = Arrangement.spacedBy(dime.sm)
                    ) {
                        if (form.isHighRisk) {
                            Card(
                                onClick = { },
                                content = { Text("پرخطر", fontSize = 10.sp) },

                            )
                        }
                        if (form.isVital) {
                            Card(
                                onClick = { },
                                content = { Text("حیاتی", fontSize = 10.sp) },

                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrandNamesSection(brandNames: List<com.approagency.pharmacy.domain.model.BrandName>) {
    val dime = LocalDime.current

    Column {
        Text(
            text = "اسامی تجاری",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = dime.sm)
        )

        brandNames.forEach { brand ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dime.sm),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(dime.sm)) {
                    Text(
                        text = brand.persianName,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    brand.manufacturer?.let {
                        Text(
                            text = "تولید کننده: $it",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}