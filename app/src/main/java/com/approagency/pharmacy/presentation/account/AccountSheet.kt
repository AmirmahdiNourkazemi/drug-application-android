package com.approagency.pharmacy.presentation.account

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.approagency.pharmacy.MainActivity
import com.approagency.pharmacy.domain.model.SubscriptionProduct
import com.approagency.pharmacy.presentation.common.CustomModalBottomSheet
import com.approagency.pharmacy.presentation.common.OtpTextField
import com.approagency.pharmacy.presentation.common.PrimaryButton
import com.approagency.pharmacy.presentation.common.shimmer
import com.approagency.pharmacy.presentation.viewModel.AccountPhase
import com.approagency.pharmacy.presentation.viewModel.AccountViewModel
import com.vada.caller.ui.theme.dime
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSheet(
    onDismiss: () -> Unit,
    viewModel: AccountViewModel = koinViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val account by viewModel.account.collectAsState()
    val activity = LocalContext.current as? Activity
    val otpAutoFillBus: OtpAutoFillBus = koinInject()

    LaunchedEffect(Unit) { viewModel.onSheetOpened() }

    // پس از خرید موفق، شیت بسته می‌شود (نوار بالای اپ خودش به‌روز می‌شود).
    LaunchedEffect(ui.purchaseSuccess) {
        if (ui.purchaseSuccess) {
            viewModel.consumePurchaseSuccess()
            onDismiss()
        }
    }

    // در مرحله‌ی کد، گوش‌دادن به پیامک را آغاز کن و با خروج متوقفش کن.
    val isOtpStep = ui.phase == AccountPhase.EnterOtp
    DisposableEffect(isOtpStep) {
        val mainActivity = activity as? MainActivity
        if (isOtpStep) mainActivity?.startOtpAutofill()
        onDispose { mainActivity?.stopOtpAutofill() }
    }

    // کدِ خوانده‌شده از پیامک را در فیلد بگذار و به‌صورت خودکار تأیید کن.
    LaunchedEffect(Unit) {
        otpAutoFillBus.codes.collect { code ->
            viewModel.updateOtp(code)
            viewModel.verifyOtp()
        }
    }

    CustomModalBottomSheet(onDismiss = onDismiss) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp)
                    .padding(MaterialTheme.dime.lg)
            ) {
                when (ui.phase) {
                    AccountPhase.EnterMobile -> {
                        SheetTitle("ورود به حساب")
                        Text(
                            "برای ادامه‌ی جستجو شماره موبایل خود را وارد کنید",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(MaterialTheme.dime.lg))
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            OutlinedTextField(
                                value = viewModel.mobile,
                                shape = MaterialTheme.shapes.large,
                                onValueChange = { viewModel.updateMobile(it) },
                                label = { Text("شماره موبایل" , textAlign = TextAlign.Right ) },
                                placeholder = { Text("09xxxxxxxxx") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(Modifier.height(MaterialTheme.dime.md))
                        PrimaryButton(text = "ارسال کد", isLoading = ui.busy, onClick = { viewModel.sendOtp() })
                    }

                    AccountPhase.EnterOtp -> {
                        SheetTitle("تأیید شماره")
                        Text(
                            "کد ارسال‌شده به ${viewModel.mobile} را وارد کنید",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(MaterialTheme.dime.lg))
                        OtpTextField(
                            otpText = viewModel.otp,
                            otpCount = 5,
                            onOtpTextChange = { value, _ -> viewModel.updateOtp(value) },
                            onComplete = { viewModel.verifyOtp() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(MaterialTheme.dime.md))
                        PrimaryButton(text = "تأیید و ورود", isLoading = ui.busy, onClick = { viewModel.verifyOtp() })
                        TextButton(onClick = { viewModel.editMobile() }, enabled = !ui.busy) {
                            Text("ویرایش شماره موبایل")
                        }
                    }

                    AccountPhase.Products -> {
                        SheetTitle("اشتراک ویژه")
                        Text(
                            "سهمیه‌ی جستجوی رایگان شما به پایان رسیده است. برای جستجوی نامحدود اشتراک تهیه کنید.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(MaterialTheme.dime.lg))
                        when {
                            ui.productsLoading -> Column(
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dime.md)
                            ) {
                                repeat(2) { ProductCardSkeleton() }
                            }
                            else -> Column(
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dime.md)
                            ) {
                                ui.products.forEach { product ->
                                    ProductCard(
                                        product = product,
                                        isPurchasing = ui.purchasingProductId == product.id,
                                        onBuy = { activity?.let { viewModel.purchase(it, product) } }
                                    )
                                }
                            }
                        }
                        if (!viewModel.gatewayAvailable) {
                            Spacer(Modifier.height(MaterialTheme.dime.sm))
                            Text(
                                "درگاه پرداخت روی این نسخه فعال نیست.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    AccountPhase.Subscribed -> {
                        SheetTitle("اشتراک فعال")
                        Text(
                            account.subscriptionTitle?.let { "اشتراک شما فعال است: $it" }
                                ?: "اشتراک شما فعال است.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                ui.error?.let { message ->
                    Spacer(Modifier.height(MaterialTheme.dime.sm))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(MaterialTheme.dime.md))
            }
        }
    }
}

@Composable
private fun SheetTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(MaterialTheme.dime.xs))
}

@Composable
private fun ProductCard(
    product: SubscriptionProduct,
    isPurchasing: Boolean,
    onBuy: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MaterialTheme.dime.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(product.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${formatPrice(product.price)} تومان",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (!product.description.isNullOrBlank()) {
                Spacer(Modifier.height(MaterialTheme.dime.xs))
                Text(
                    product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(MaterialTheme.dime.md))
            PrimaryButton(text = "خرید", height = 44, isLoading = isPurchasing, onClick = onBuy)
        }
    }
}

/** جای‌گیرنده‌ی shimmer برای کارت محصول هنگام بارگذاری. */
@Composable
private fun ProductCardSkeleton() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MaterialTheme.dime.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier
                        .width(120.dp)
                        .height(18.dp)
                        .shimmer()
                )
                Box(
                    Modifier
                        .width(72.dp)
                        .height(18.dp)
                        .shimmer()
                )
            }
            Spacer(Modifier.height(MaterialTheme.dime.md))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .shimmer(shape = MaterialTheme.shapes.medium)
            )
        }
    }
}

/** قالب‌بندی قیمت با جداکننده‌ی هزارگان. */
private fun formatPrice(price: Long): String =
    price.toString().reversed().chunked(3).joinToString(",").reversed()
