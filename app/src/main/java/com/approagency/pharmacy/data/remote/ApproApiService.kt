package com.approagency.pharmacy.data.remote

import com.approagency.pharmacy.data.dto.CheckOtpResponse
import com.approagency.pharmacy.data.dto.LoginOtpResponse
import com.approagency.pharmacy.data.dto.ProductDto
import com.approagency.pharmacy.data.dto.StatusDto
import com.approagency.pharmacy.data.dto.SubscribeRequest
import com.approagency.pharmacy.data.dto.SubscribeResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** بک‌اند اشتراک/احراز هویت آپرواجنسی ([Config.AUTH_BASE_URL]). */
interface ApproApiService {

    /** ارسال کد یک‌بارمصرف به موبایل. */
    @FormUrlEncoded
    @POST("auth/login-otp")
    suspend fun loginOtp(
        @Field("mobile") mobile: String,
        @Field("package_name") packageName: String,
        @Field("fcm_token") fcmToken: String? = null
    ): LoginOtpResponse

    /** تأیید کد و دریافت توکن نشست. */
    @FormUrlEncoded
    @POST("auth/check-otp")
    suspend fun checkOtp(
        @Field("mobile") mobile: String,
        @Field("token") token: String
    ): CheckOtpResponse

    /** خروج از حساب (توکن از طریق اینترسپتور ارسال می‌شود). */
    @PUT("auth/logout")
    suspend fun logout()

    /** وضعیت اشتراک کاربر برای این پکیج. */
    @GET("status")
    suspend fun getStatus(
        @Query("package_name") packageName: String
    ): StatusDto

    /** فهرست محصولات اشتراک قابل خرید برای این پکیج. */
    @GET("package-names/{name}/products")
    suspend fun getProducts(
        @Path("name") packageName: String
    ): List<ProductDto>

    /** ثبت خرید اشتراک پس از پرداخت موفق در درگاه (مایکت/بازار). */
    @PUT("package-names/{name}/products/{product_id}/subscribe")
    suspend fun subscribe(
        @Path("name") packageName: String,
        @Path("product_id") productId: Int,
        @Body body: SubscribeRequest
    ): SubscribeResponse
}
