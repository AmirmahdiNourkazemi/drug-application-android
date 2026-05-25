package com.approagency.drug.di


import android.app.Application
import com.approagency.drug.data.local.database.LabDatabase
import com.approagency.drug.data.remote.DarooyabApiService
import com.approagency.drug.data.remote.DrugApiService
import com.approagency.drug.data.remote.DrugHtmlParser
import com.approagency.drug.data.repository.DrugRepositoryImpl
import com.approagency.drug.data.repository.LabRepositoryImpl
import com.approagency.drug.domain.repository.DrugRepository
import com.approagency.drug.domain.usecase.GetDarmanUseCase
import com.approagency.drug.domain.usecase.GetDrugDetailUseCase
import com.approagency.drug.domain.usecase.GetDrugSearchUseCase
import com.approagency.drug.domain.usecase.GetTestGroupUseCase
import com.approagency.drug.domain.usecase.GetTestItemByGroupId
import com.approagency.drug.domain.usecase.SearchDrugsYabUseCase
import com.approagency.drug.domain.usecase.SearchTestsUseCase
import com.approagency.drug.presentation.viewModel.HomeViewModel
import com.approagency.drug.presentation.viewModel.LabViewModel
import com.approagency.drug.utils.Config
import com.approgency.drug.presentation.viewModel.SearchViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

val appModule= module {

    single { LabDatabase.getInstance(androidContext()) }

    single { get<LabDatabase>().testGroupDao() }
    single { get<LabDatabase>().testItemDao() }

    // Shared OkHttpClient for both APIs
    single {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY  // Change to BODY for debugging
            })
            // Add cookie support
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    // ========== Retrofit for YOUR JSON API ==========
    single {
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<DrugApiService> {
        val retrofit: Retrofit = get()
        retrofit.create(DrugApiService::class.java)
    }

    // ========== Retrofit for DAROOYAB Website (HTML response) ==========
    // IMPORTANT: Use ScalarsConverterFactory for plain text/HTML, NOT Gson!
    single {
        Retrofit.Builder()
            .baseUrl(Config.Darro_Url)
            .client(get<OkHttpClient>())
            .addConverterFactory(ScalarsConverterFactory.create()) // This handles String responses
            .build()
    }

    // Create the API service from the Scalars-based Retrofit instance
    single<DarooyabApiService> {
        val retrofit: Retrofit = get()  // Gets the Scalars Retrofit instance
        retrofit.create(DarooyabApiService::class.java)
    }


    factory { DrugHtmlParser() }

    //repo
    single<DrugRepository> {
        DrugRepositoryImpl(get<DrugApiService>() , get<DarooyabApiService>() , get())
    }



    //UseCase
    single {
        GetDrugSearchUseCase(get())

    }

    single {
        SearchDrugsYabUseCase(get())
    }

    single{
        GetDrugDetailUseCase(get())
    }

    single {
        GetDarmanUseCase(get())
    }

    single {
        GetTestGroupUseCase(get())
    }

    single {
        GetTestItemByGroupId(get())
    }

    single { LabRepositoryImpl(get(), get()) }

    single { SearchTestsUseCase(get()) }
    //view model
    viewModel {
        HomeViewModel(get() , get() , get())
    }

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        LabViewModel(get() , get() , get())
    }
}