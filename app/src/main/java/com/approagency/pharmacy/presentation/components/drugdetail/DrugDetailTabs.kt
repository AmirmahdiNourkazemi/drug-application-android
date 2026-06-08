package com.approagency.pharmacy.presentation.components.drugdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.approagency.pharmacy.domain.model.DrugDetail
import com.approagency.pharmacy.domain.model.GenericInfo
import com.vada.caller.ui.theme.LocalDime

/** کارت اطلاعات سازنده (برای صفحات برند). */
@Composable
internal fun ManufacturerInfoCard(
    manufacturer: String,
    genericInfo: GenericInfo?,
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

/** تب «عمومی». */
@Composable
internal fun GeneralInfoTabContent(drugDetail: DrugDetail) {
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
        } else {
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

/** تب «تخصصی». */
@Composable
internal fun SpecializedInfoTabContent(drugDetail: DrugDetail) {
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

/** تب «اشکال دارویی». */
@Composable
internal fun DosageFormsTabContent(drugDetail: DrugDetail) {
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

/** تب «اسامی تجاری». */
@Composable
internal fun BrandNamesTabContent(drugDetail: DrugDetail) {
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
