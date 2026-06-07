package com.approagency.pharmacy.presentation.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.approagency.pharmacy.domain.model.DrugDetail
import com.approagency.pharmacy.presentation.common.ErrorState
import com.approagency.pharmacy.presentation.common.Loading
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
                                            text = state.drugDetail.englishName.take(30),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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
                            // Manufacturer Info Card (برای صفحات برند)
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
                                                fontSize = 13.sp,
                                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1
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
                                        item { GeneralInfoTabContent(drugDetail) }
                                    }

                                    1 -> {
                                        item { SpecializedInfoTabContent(drugDetail) }
                                    }

                                    2 -> {
                                        item { DosageFormsTabContent(drugDetail) }
                                    }

                                    3 -> {
                                        item { BrandNamesTabContent(drugDetail) }
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
}

@Composable
fun ManufacturerInfoCard(
    manufacturer: String,
    genericInfo: com.approagency.pharmacy.domain.model.GenericInfo?,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(dime.sm)
    ) {
        Column(
            modifier = Modifier.padding(dime.md),
            verticalArrangement = Arrangement.spacedBy(dime.xs)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dime.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "سازنده:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = manufacturer,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (genericInfo != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dime.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ماده موثره:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = genericInfo.persianName,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// آپدیت GeneralInfoTabContent برای صفحات Generic
@Composable
fun GeneralInfoTabContent(drugDetail: DrugDetail) {
    val dime = LocalDime.current

    Column(verticalArrangement = Arrangement.spacedBy(dime.md)) {

        if (drugDetail.isGeneric) {
            // ========== صفحات ژنریک (Generic) ==========

            // اگر generalInfo وجود دارد (از EtelaatOmomiVaTakhasosi یا EtelaatOmomi)
            if (!drugDetail.generalInfo.isNullOrBlank()) {
                InfoSectionCard(
                    title = "اطلاعات عمومی",
                    content = drugDetail.generalInfo
                )
            }
            // اگر specializedInfo وجود دارد (از EtelaatTakhasosiContent)
            else if (!drugDetail.specializedInfo.isNullOrBlank()) {
                InfoSectionCard(
                    title = "اطلاعات تخصصی",
                    content = drugDetail.specializedInfo
                )
            }
            // در غیر این صورت، اطلاعات پراکنده را نمایش بده
            else {
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
                                fontSize = 15.sp,
                                color = Color(0xFFE65100)
                            )
                            drugDetail.pregnancyCategory?.let {
                                Text(
                                    text = "گروه: $it",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            drugDetail.pregnancyDescription?.let {
                                Spacer(modifier = Modifier.height(dime.xs))
                                Text(
                                    text = it,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // موارد مصرف
                drugDetail.usage?.let {
                    InfoSectionCard(title = "موارد مصرف", content = it)
                }

                // موارد منع مصرف
                drugDetail.contraindications?.let {
                    InfoSectionCard(title = "موارد منع مصرف", content = it)
                }

                // عوارض جانبی
                drugDetail.sideEffects?.let {
                    InfoSectionCard(title = "عوارض جانبی", content = it)
                }

                // هشدارها
                drugDetail.warnings?.let {
                    InfoSectionCard(title = "هشدارها", content = it)
                }

                // توصیه‌های دارویی
                drugDetail.recommendations?.let {
                    InfoSectionCard(title = "توصیه‌های دارویی", content = it)
                }
            }

            // اگر هیچ اطلاعاتی وجود نداشت
            if (drugDetail.drugClass == null &&
                drugDetail.therapeuticClass == null &&
                drugDetail.pregnancyCategory == null &&
                drugDetail.usage == null &&
                drugDetail.contraindications == null &&
                drugDetail.sideEffects == null &&
                drugDetail.warnings == null &&
                drugDetail.generalInfo.isNullOrBlank() &&
                drugDetail.specializedInfo.isNullOrBlank()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "اطلاعات عمومی برای این دارو ثبت نشده است",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }  else {
            // ========== صفحات برند (Brand) ==========
            // نمایش اطلاعات تخصصی برند که در بخش brandAttrPersDesc است
            drugDetail.usage?.let {
                InfoSectionCard(title = "موارد مصرف", content = it)
            }
            drugDetail.contraindications?.let {
                InfoSectionCard(title = "موارد منع مصرف", content = it)
            }
            drugDetail.sideEffects?.let {
                InfoSectionCard(title = "عوارض جانبی", content = it)
            }
            drugDetail.warnings?.let {
                InfoSectionCard(title = "هشدارها", content = it)
            }
            drugDetail.recommendations?.let {
                InfoSectionCard(title = "توصیه‌های دارویی", content = it)
            }

            if (drugDetail.usage == null &&
                drugDetail.contraindications == null &&
                drugDetail.sideEffects == null &&
                drugDetail.warnings == null
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "اطلاعات عمومی برای این برند ثبت نشده است.\nبرای مشاهده اطلاعات کامل، به صفحه داروی ژنریک مراجعه کنید.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun SpecializedInfoTabContent(drugDetail: DrugDetail) {
    val dime = LocalDime.current

    Column(verticalArrangement = Arrangement.spacedBy(dime.md)) {

        // برای صفحات برند، اطلاعات تخصصی معمولاً در قسمت brandAttrPersDesc است
        // که در پارسر ما به صورت usage, contraindications, sideEffects, warnings, recommendations ذخیره شده

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

        // اگر هیچ اطلاعات تخصصی وجود نداشت
        if (drugDetail.usage == null &&
            drugDetail.mechanism == null &&
            drugDetail.contraindications == null &&
            drugDetail.sideEffects == null &&
            drugDetail.warnings == null
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (drugDetail.isGeneric)
                        "اطلاعات تخصصی برای این دارو ثبت نشده است"
                    else
                        "اطلاعات تخصصی برای این برند ثبت نشده است.\nبرای مشاهده اطلاعات کامل، به صفحه داروی ژنریک مراجعه کنید.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DosageFormsTabContent(drugDetail: DrugDetail) {
    val dime = LocalDime.current
    val dosageForms = drugDetail.dosageForms

    if (dosageForms.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (drugDetail.isGeneric)
                    "اشکال دارویی برای این دارو ثبت نشده است"
                else
                    "سایر اشکال دارویی این برند ثبت نشده است",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(dime.sm)) {
            // اگر صفحه برند است، عنوان متفاوت نشان بده
            if (!drugDetail.isGeneric) {
                Text(
                    text = "سایر محصولات این برند",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = dime.xs)
                )
            }

            dosageForms.forEach { form ->
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
                            text = form.persianName,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        if (form.englishName.isNotBlank()) {
                            Text(
                                text = form.englishName,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // نشانگرهای خطر
                        if (form.isHighRisk || form.isVital) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(dime.xs)
                            ) {
                                if (form.isHighRisk) {
                                    androidx.compose.material3.Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFFFEBEE)
                                    ) {
                                        Text(
                                            text = "پرخطر",
                                            fontSize = 10.sp,
                                            color = Color(0xFFC62828),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = dime.xxs)
                                        )
                                    }
                                }
                                if (form.isVital) {
                                    androidx.compose.material3.Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFE8F5E9)
                                    ) {
                                        Text(
                                            text = "حیاتی",
                                            fontSize = 10.sp,
                                            color = Color(0xFF2E7D32),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = dime.xxs)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrandNamesTabContent(drugDetail: DrugDetail) {
    val dime = LocalDime.current

    if (drugDetail.isGeneric && drugDetail.brandNames.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "اسامی تجاری برای این دارو ثبت نشده است",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    } else if (drugDetail.isGeneric && drugDetail.brandNames.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(dime.sm)) {
            drugDetail.brandNames.forEach { brand ->
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
    } else {
        // صفحه برند است - اسامی تجاری برای برند معنی ندارد
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "این صفحه مربوط به یک محصول تجاری است. برای مشاهده اسامی تجاری سایر برندها، به صفحه داروی ژنریک مراجعه کنید.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
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
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Text(
                text = content,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        }
    }
}