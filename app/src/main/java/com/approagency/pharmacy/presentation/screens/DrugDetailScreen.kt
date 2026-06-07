package com.approagency.pharmacy.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
                                                style = MaterialTheme.typography.labelLarge,
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
                                    .padding(horizontal = dime.md),
                                contentPadding = PaddingValues(top = dime.md, bottom = dime.xxl),
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
    val onContainer = MaterialTheme.colorScheme.onSecondaryContainer

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(dime.md),
        verticalArrangement = Arrangement.spacedBy(dime.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dime.sm)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = onContainer
            )
            Text(
                text = "سازنده:",
                style = MaterialTheme.typography.labelLarge,
                color = onContainer
            )
            Text(
                text = manufacturer,
                style = MaterialTheme.typography.bodyMedium,
                color = onContainer
            )
        }

        if (genericInfo != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dime.sm)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = onContainer
                )
                Text(
                    text = "ماده موثره:",
                    style = MaterialTheme.typography.labelLarge,
                    color = onContainer
                )
                Text(
                    text = genericInfo.persianName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onContainer
                )
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
                    DetailCard {
                        Column(verticalArrangement = Arrangement.spacedBy(dime.xs)) {
                            drugDetail.drugClass?.let {
                                LabeledRow(label = "طبقه بندی مارتیندل", value = it)
                            }
                            drugDetail.therapeuticClass?.let {
                                LabeledRow(label = "طبقه درمانی", value = it)
                            }
                        }
                    }
                }

                // Pregnancy Card
                if (drugDetail.pregnancyCategory != null || drugDetail.pregnancyDescription != null) {
                    val onContainer = MaterialTheme.colorScheme.onTertiaryContainer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(dime.md),
                        verticalArrangement = Arrangement.spacedBy(dime.xs)
                    ) {
                        Text(
                            text = "مصرف در بارداری",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = onContainer
                        )
                        drugDetail.pregnancyCategory?.let {
                            Text(
                                text = "گروه: $it",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = onContainer
                            )
                        }
                        drugDetail.pregnancyDescription?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp,
                                color = onContainer.copy(alpha = 0.9f)
                            )
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
                DetailEmptyState(text = "اطلاعات عمومی برای این دارو ثبت نشده است")
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
                DetailEmptyState(
                    text = "اطلاعات عمومی برای این برند ثبت نشده است.\nبرای مشاهده اطلاعات کامل، به صفحه داروی ژنریک مراجعه کنید."
                )
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
            DetailEmptyState(
                text = if (drugDetail.isGeneric)
                    "اطلاعات تخصصی برای این دارو ثبت نشده است"
                else
                    "اطلاعات تخصصی برای این برند ثبت نشده است.\nبرای مشاهده اطلاعات کامل، به صفحه داروی ژنریک مراجعه کنید."
            )
        }
    }
}

@Composable
fun DosageFormsTabContent(drugDetail: DrugDetail) {
    val dime = LocalDime.current
    val dosageForms = drugDetail.dosageForms

    if (dosageForms.isEmpty()) {
        DetailEmptyState(
            text = if (drugDetail.isGeneric)
                "اشکال دارویی برای این دارو ثبت نشده است"
            else
                "سایر اشکال دارویی این برند ثبت نشده است"
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(dime.sm)) {
            // اگر صفحه برند است، عنوان متفاوت نشان بده
            if (!drugDetail.isGeneric) {
                SectionHeader(
                    title = "سایر محصولات این برند",
                    modifier = Modifier.padding(bottom = dime.xs)
                )
            }

            dosageForms.forEach { form ->
                DetailCard {
                    Text(
                        text = form.persianName,
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (form.englishName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(dime.xxs))
                        Text(
                            text = form.englishName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // نشانگرهای خطر
                    if (form.isHighRisk || form.isVital) {
                        Spacer(modifier = Modifier.height(dime.sm))
                        Row(horizontalArrangement = Arrangement.spacedBy(dime.xs)) {
                            if (form.isHighRisk) {
                                TagChip(
                                    text = "پرخطر",
                                    container = MaterialTheme.colorScheme.errorContainer,
                                    onContainer = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            if (form.isVital) {
                                TagChip(
                                    text = "حیاتی",
                                    container = MaterialTheme.colorScheme.primaryContainer,
                                    onContainer = MaterialTheme.colorScheme.onPrimaryContainer
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
fun BrandNamesTabContent(drugDetail: DrugDetail) {
    val dime = LocalDime.current

    if (drugDetail.isGeneric && drugDetail.brandNames.isEmpty()) {
        DetailEmptyState(text = "اسامی تجاری برای این دارو ثبت نشده است")
    } else if (drugDetail.isGeneric && drugDetail.brandNames.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(dime.sm)) {
            drugDetail.brandNames.forEach { brand ->
                DetailCard {
                    Text(
                        text = brand.persianName,
                        style = MaterialTheme.typography.titleSmall
                    )
                    brand.manufacturer?.let {
                        Spacer(modifier = Modifier.height(dime.xs))
                        LabeledRow(label = "تولید کننده", value = it)
                    }
                    brand.importer?.let {
                        Spacer(modifier = Modifier.height(dime.xxs))
                        LabeledRow(label = "وارد کننده", value = it)
                    }
                }
            }
        }
    } else {
        // صفحه برند است - اسامی تجاری برای برند معنی ندارد
        DetailEmptyState(
            text = "این صفحه مربوط به یک محصول تجاری است. برای مشاهده اسامی تجاری سایر برندها، به صفحه داروی ژنریک مراجعه کنید."
        )
    }
}

@Composable
fun InfoSectionCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    DetailCard(modifier = modifier) {
        SectionHeader(title = title)
        Spacer(modifier = Modifier.height(dime.sm))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * کارت پایه‌ی صفحه‌ی جزئیات — ظاهر یکدست با بقیه‌ی برنامه:
 * سطح روشن + قاب نازک هم‌رنگ تم، بدون سایه‌ی سنگین.
 */
@Composable
private fun DetailCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = MaterialTheme.shapes.large
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = shape
            )
            .padding(MaterialTheme.dime.md),
        content = content
    )
}

/** سربرگ بخش با نوار تأکید کوچک کنار عنوان. */
@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dime.sm)
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 18.dp)
                .clip(RoundedCornerShape(dime.xxs))
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/** حالت «اطلاعاتی ثبت نشده» با آیکن، یکدست در همه‌ی تب‌ها. */
@Composable
private fun DetailEmptyState(
    text: String,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dime.xxl, horizontal = dime.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dime.sm)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** ردیف «برچسب: مقدار» با تایپوگرافی یکدست. */
@Composable
private fun LabeledRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }
}

/** برچسب کوچک رنگی (مثل «پرخطر» / «حیاتی»). */
@Composable
private fun TagChip(
    text: String,
    container: Color,
    onContainer: Color
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = onContainer,
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.dime.xs))
            .background(container)
            .padding(horizontal = MaterialTheme.dime.sm, vertical = MaterialTheme.dime.xxs)
    )
}