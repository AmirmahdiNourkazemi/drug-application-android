package com.approagency.drug.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.navigation.NavController
import com.approagency.drug.domain.model.DrugDetail
import com.approagency.drug.presentation.common.ErrorState
import com.approagency.drug.presentation.viewModel.DrugDetailViewModel
import com.approagency.drug.presentation.viewModel.DrugDetailYabState
import com.vada.caller.ui.theme.LocalDime
import com.vada.caller.ui.theme.dime
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = detailState) {
                        is DrugDetailYabState.Success -> {
                            Column {
                                Text(
                                    text = state.drugDetail.persianName.take(20),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                                if (state.drugDetail.englishName.isNotEmpty()) {
                                    Text(
                                        text = state.drugDetail.englishName.take(25),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        else -> {
                            Text("جزئیات دارو", fontSize = 16.sp)
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
                DrugDetailYabState.Idle -> {
                    // Initial state
                }

                DrugDetailYabState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(dime.md))
                            Text(
                                text = "در حال دریافت اطلاعات دارو...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                is DrugDetailYabState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Tab Row
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
                                            fontSize = 14.sp,
                                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }

                        // Tab Content
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = dime.md)
                                .padding(top = dime.md),
                            verticalArrangement = Arrangement.spacedBy(dime.md)
                        ) {
                            when (selectedTab) {
                                0 -> {
                                    item { GeneralInfoTabContent(state.drugDetail) }
                                }
                                1 -> {
                                    item { SpecializedInfoTabContent(state.drugDetail) }
                                }
                                2 -> {
                                    item { DosageFormsTabContent(state.drugDetail.dosageForms) }
                                }
                                3 -> {
                                    item { BrandNamesTabContent(state.drugDetail.brandNames) }
                                }
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

@Composable
fun GeneralInfoTabContent(drugDetail: DrugDetail) {
    val dime = LocalDime.current

    Column(verticalArrangement = Arrangement.spacedBy(dime.md)) {
        // Categories Card
        if (drugDetail.drugClass != null || drugDetail.therapeuticClass != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(dime.sm)
            ) {
                Column(
                    modifier = Modifier.padding(dime.md),
                    verticalArrangement = Arrangement.spacedBy(dime.xs)
                ) {
                    drugDetail.drugClass?.let {
                        Row {
                            Text(
                                text = "طبقه بندی مارتیندل: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    drugDetail.therapeuticClass?.let {
                        Row {
                            Text(
                                text = "طبقه درمانی: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Pregnancy Card
        if (drugDetail.pregnancyCategory != null || drugDetail.pregnancyDescription != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                shape = RoundedCornerShape(dime.sm)
            ) {
                Column(
                    modifier = Modifier.padding(dime.md),
                    verticalArrangement = Arrangement.spacedBy(dime.xs)
                ) {
                    Text(
                        text = "مصرف در بارداری",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFE65100)
                    )
                    drugDetail.pregnancyCategory?.let {
                        Text(
                            text = "گروه: $it",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    drugDetail.pregnancyDescription?.let {
                        Text(
                            text = it,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Usage Section
        drugDetail.usage?.let {
            InfoSectionCard(title = "موارد مصرف", content = it)
        }

        // Contraindications Section
        drugDetail.contraindications?.let {
            InfoSectionCard(title = "موارد منع مصرف", content = it)
        }

        // Side Effects Section
        drugDetail.sideEffects?.let {
            InfoSectionCard(title = "عوارض جانبی", content = it)
        }

        // Warnings Section
        drugDetail.warnings?.let {
            InfoSectionCard(title = "هشدارها", content = it)
        }
    }
}

@Composable
fun SpecializedInfoTabContent(drugDetail: DrugDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        drugDetail.usage?.let {
            InfoSectionCard(title = "موارد مصرف", content = it)
        }
        drugDetail.mechanism?.let {
            InfoSectionCard(title = "مکانیسم اثر", content = it)
        }
        drugDetail.pharmacokinetics?.let {
            InfoSectionCard(title = "فارماکوکینتیک", content = it)
        }
        drugDetail.contraindications?.let {
            InfoSectionCard(title = "موارد منع مصرف", content = it)
        }
        drugDetail.sideEffects?.let {
            InfoSectionCard(title = "عوارض جانبی", content = it)
        }
        drugDetail.interactions?.let {
            InfoSectionCard(title = "تداخلات دارویی", content = it)
        }
        drugDetail.warnings?.let {
            InfoSectionCard(title = "هشدارها", content = it)
        }
        drugDetail.recommendations?.let {
            InfoSectionCard(title = "توصیه‌های دارویی", content = it)
        }

        if (drugDetail.usage == null &&
            drugDetail.mechanism == null &&
            drugDetail.contraindications == null &&
            drugDetail.sideEffects == null
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "اطلاعات تخصصی برای این دارو ثبت نشده است",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DosageFormsTabContent(dosageForms: List<com.approagency.drug.domain.model.DosageForm>) {
    val dime = LocalDime.current

    if (dosageForms.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "اشکال دارویی برای این دارو ثبت نشده است",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(dime.sm)) {
            dosageForms.forEach { form ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(dime.sm)
                ) {
                    Column(
                        modifier = Modifier.padding(dime.md),
                        verticalArrangement = Arrangement.spacedBy(dime.xs)
                    ) {
                        Text(
                            text = form.persianName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (form.englishName.isNotBlank()) {
                            Text(
                                text = form.englishName,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(dime.sm)
                        ) {
                            if (form.isHighRisk) {
                                androidx.compose.material3.Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFFEBEE),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "پرخطر",
                                        fontSize = 10.sp,
                                        color = Color(0xFFC62828),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (form.isVital) {
                                androidx.compose.material3.Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFE8F5E9),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "حیاتی",
                                        fontSize = 10.sp,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        if (!form.warningLabel.isNullOrBlank()) {
                            Text(
                                text = "⚠️ ${form.warningLabel}",
                                fontSize = 11.sp,
                                color = Color(0xFFE65100),
                                modifier = Modifier.padding(top = dime.xs)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrandNamesTabContent(brandNames: List<com.approagency.drug.domain.model.BrandName>) {
    val dime = LocalDime.current

    if (brandNames.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "اسامی تجاری برای این دارو ثبت نشده است",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(dime.sm)) {
            brandNames.forEach { brand ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(dime.sm)
                ) {
                    Column(
                        modifier = Modifier.padding(dime.md),
                        verticalArrangement = Arrangement.spacedBy(dime.xs)
                    ) {
                        Text(
                            text = brand.persianName,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        brand.manufacturer?.let {
                            Row {
                                Text(
                                    text = "تولید کننده: ",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = it,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        brand.importer?.let {
                            Row {
                                Text(
                                    text = "وارد کننده: ",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = it,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoSectionCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(dime.sm)
    ) {
        Column(
            modifier = Modifier.padding(dime.md),
            verticalArrangement = Arrangement.spacedBy(dime.xs)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        }
    }
}