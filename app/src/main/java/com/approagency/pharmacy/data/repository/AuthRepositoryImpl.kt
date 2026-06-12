package com.approagency.pharmacy.data.repository

import com.approagency.pharmacy.data.dto.SubscribeRequest
import com.approagency.pharmacy.data.local.SessionManager
import com.approagency.pharmacy.data.remote.ApproApiService
import com.approagency.pharmacy.domain.model.SubscriptionProduct
import com.approagency.pharmacy.domain.repository.AuthRepository
import com.approagency.pharmacy.utils.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val api: ApproApiService,
    private val session: SessionManager
) : AuthRepository {

    override suspend fun loginOtp(mobile: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.loginOtp(mobile = mobile, packageName = Config.PACKAGE_NAME)
            session.saveMobile(mobile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkOtp(mobile: String, code: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.checkOtp(mobile = mobile, token = code)
                val token = response.token
                if (token.isNullOrBlank()) {
                    Result.failure(IllegalStateException("توکن دریافت نشد."))
                } else {
                    session.saveToken(token)
                    session.saveMobile(mobile)
                    // بلافاصله وضعیت اشتراک را بخوان تا حساب به‌روز شود.
                    runCatching { fetchAndStoreStatus() }
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            runCatching { api.logout() } // حتی اگر سرور خطا داد، نشست محلی پاک شود
            session.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            session.clear()
            Result.failure(e)
        }
    }

    override suspend fun refreshStatus(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            Result.success(fetchAndStoreStatus())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** `/status` را می‌خواند، حساب را ذخیره می‌کند و وضعیت اشتراک را برمی‌گرداند. */
    private suspend fun fetchAndStoreStatus(): Boolean {
        val dto = api.getStatus(packageName = Config.PACKAGE_NAME)
        session.updateAccount(
            mobile = dto.mobile ?: session.account.value.mobile,
            displayName = dto.displayName,
            isSubscribed = dto.isSubscribed,
            subscriptionTitle = dto.subscriptionTitle,
            subscriptionExpireAt = dto.subscriptionExpireAt
        )
        return dto.isSubscribed
    }

    override suspend fun getProducts(): Result<List<SubscriptionProduct>> =
        withContext(Dispatchers.IO) {
            try {
                val products = api.getProducts(packageName = Config.PACKAGE_NAME).map {
                    SubscriptionProduct(
                        id = it.id,
                        title = it.title ?: "",
                        price = it.price ?: 0L,
                        uuid = it.uuid,
                        description = it.descriptions
                    )
                }
                Result.success(products)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun subscribe(
        productId: Int,
        purchaseToken: String,
        gateway: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.subscribe(
                packageName = Config.PACKAGE_NAME,
                productId = productId,
                body = SubscribeRequest(purchaseToken = purchaseToken, gateway = gateway)
            )
            // پس از ثبت خرید، وضعیت واقعی را از سرور بخوان.
            runCatching { fetchAndStoreStatus() }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
