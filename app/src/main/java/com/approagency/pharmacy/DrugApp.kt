package com.approagency.pharmacy

import android.app.Application
import com.approagency.pharmacy.data.local.database.LabDatabase
import com.approagency.pharmacy.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DrugApp : Application() {

    // اسکوپ سطح‌اپ برای کارهای پس‌زمینه‌ی طول‌عمرِ برنامه (به‌جای GlobalScope).
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DrugApp)
            modules(appModule)
            printLogger()
        }

        // پیش‌بارگذاری دیتابیس برای دسترسی سریع‌تر
        applicationScope.launch {
            LabDatabase.getInstance(this@DrugApp)
        }
    }
}
