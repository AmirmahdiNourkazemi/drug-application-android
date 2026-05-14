package com.approagency.drug.di


import android.app.Application
import com.approagency.drug.data.local.database.LabDatabase
import com.approagency.drug.data.remote.DrugApiService
import com.approagency.drug.data.repository.DrugRepositoryImpl
import com.approagency.drug.data.repository.LabRepositoryImpl
import com.approagency.drug.domain.repository.DrugRepository
import com.approagency.drug.domain.usecase.GetDarmanUseCase
import com.approagency.drug.domain.usecase.GetDrugDetailUseCase
import com.approagency.drug.domain.usecase.GetDrugSearchUseCase
import com.approagency.drug.domain.usecase.GetTestGroupUseCase
import com.approagency.drug.domain.usecase.GetTestItemByGroupId
import com.approagency.drug.domain.usecase.SearchTestsUseCase
import com.approagency.drug.presentation.viewModel.HomeViewModel
import com.approagency.drug.presentation.viewModel.LabViewModel
import com.approagency.drug.utils.Config
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule= module {

    single { LabDatabase.getInstance(androidContext()) }

    single { get<LabDatabase>().testGroupDao() }
    single { get<LabDatabase>().testItemDao() }

    single {
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<DrugApiService> {
        get<Retrofit>().create(DrugApiService::class.java)
    }

    //repo
    single<DrugRepository> {
        DrugRepositoryImpl(get())
    }



    //UseCase
    single {
        GetDrugSearchUseCase(get())

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
        LabViewModel(get() , get() , get())
    }
}