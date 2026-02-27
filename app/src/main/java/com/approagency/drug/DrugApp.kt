package com.approagency.drug

import android.app.Application
import com.approagency.drug.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DrugApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DrugApp)
            modules(appModule)
            printLogger()
        }
    }
}