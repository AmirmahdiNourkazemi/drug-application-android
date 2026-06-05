package com.approagency.pharmacy

import android.app.Application
import com.approagency.pharmacy.data.local.database.LabDatabase
import com.approagency.pharmacy.di.appModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

        // Preload database (optional, for faster access)
        GlobalScope.launch {
            LabDatabase.getInstance(this@DrugApp)
        }
    }
}