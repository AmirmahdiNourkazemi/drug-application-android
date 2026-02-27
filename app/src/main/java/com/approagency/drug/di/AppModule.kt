package com.approagency.drug.di


import android.app.Application
import com.approagency.drug.data.repository.DrugRepositoryImpl
import com.approagency.drug.domain.repository.DrugRepository
import com.approagency.drug.domain.usecase.GetDrugSearchUseCase
import com.approagency.drug.presentation.viewModel.HomeViewModel
import com.approagency.drug.utils.Config
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule= module {
    single {
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    //repo
    single<DrugRepository> {
        DrugRepositoryImpl(get())
    }



    //UseCase
    single {
        GetDrugSearchUseCase(get())
    }

    //view model
    viewModel { (app: Application) ->
        HomeViewModel(app, get())}
}