package com.approagency.pharmacy.di


import com.approagency.pharmacy.data.local.database.LabDatabase
import com.approagency.pharmacy.data.remote.DarooyabApiService
import com.approagency.pharmacy.data.remote.DrugApiService
import com.approagency.pharmacy.data.remote.DrugDetailParser
import com.approagency.pharmacy.data.remote.DrugHtmlParser
import com.approagency.pharmacy.data.repository.DrugRepositoryImpl
import com.approagency.pharmacy.data.repository.LabRepositoryImpl
import com.approagency.pharmacy.domain.repository.DrugRepository
import com.approagency.pharmacy.domain.usecase.DrugDetailYabUseCase
import com.approagency.pharmacy.domain.usecase.GetDarmanUseCase
import com.approagency.pharmacy.domain.usecase.GetDrugDetailUseCase
import com.approagency.pharmacy.domain.usecase.GetDrugSearchUseCase
import com.approagency.pharmacy.domain.usecase.GetPharmaciesUseCase
import com.approagency.pharmacy.domain.usecase.GetPharmacyDetailUseCase
import com.approagency.pharmacy.domain.usecase.GetTestGroupUseCase
import com.approagency.pharmacy.domain.usecase.GetTestItemByGroupId
import com.approagency.pharmacy.domain.usecase.SearchDrugsYabUseCase
import com.approagency.pharmacy.domain.usecase.SearchTestsUseCase
import com.approagency.pharmacy.presentation.viewModel.DrugDetailViewModel
import com.approagency.pharmacy.presentation.viewModel.HomeViewModel
import com.approagency.pharmacy.presentation.viewModel.LabViewModel
import com.approagency.pharmacy.presentation.viewModel.PharmacyViewModel
import com.approagency.pharmacy.utils.Config
import com.approagency.pharmacy.presentation.viewModel.SearchViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
val jsonRetrofitQualifier = qualifier("jsonRetrofit")
val scalarRetrofitQualifier = qualifier("scalarRetrofit")
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

    single<Retrofit>(jsonRetrofitQualifier) {  // Add qualifier
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<DrugApiService> {
        val retrofit: Retrofit = get(jsonRetrofitQualifier)  // Get qualified instance
        retrofit.create(DrugApiService::class.java)
    }

    // ========== Retrofit for DAROOYAB Website ==========
    single<Retrofit>(scalarRetrofitQualifier) {  // Add qualifier
        Retrofit.Builder()
            .baseUrl(Config.Darro_Url)
            .client(get<OkHttpClient>())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    single<DarooyabApiService> {
        val retrofit: Retrofit = get(scalarRetrofitQualifier)  // Get qualified instance
        retrofit.create(DarooyabApiService::class.java)
    }


    factory { DrugHtmlParser() }
    factory { DrugDetailParser() }
    //repo
    single<DrugRepository> {
        DrugRepositoryImpl(get<DrugApiService>() , get<DarooyabApiService>() , get() , get())
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
        GetPharmaciesUseCase(get())
    }

    single {
        GetTestItemByGroupId(get())
    }

    single { LabRepositoryImpl(get(), get()) }

    single {
        DrugDetailYabUseCase(get())
    }

    single {
        GetPharmacyDetailUseCase(get())
    }

    viewModel {
        DrugDetailViewModel(get())
    }

    single { SearchTestsUseCase(get()) }
    //view model
    viewModel {
        HomeViewModel(get() , get() , get())
    }

    viewModel {
        SearchViewModel(get())
    }
    viewModel {
        PharmacyViewModel(get() , get ())
    }

    viewModel {
        LabViewModel(get() , get() , get())
    }
}