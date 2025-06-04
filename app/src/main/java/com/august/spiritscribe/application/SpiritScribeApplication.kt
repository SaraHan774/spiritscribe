package com.august.spiritscribe.application

import android.app.Application
import com.august.spiritscribe.utils.ResourceUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.august.spiritscribe.data.local.SpiritScribeDatabase

@HiltAndroidApp
class SpiritScribeApplication : Application() {
    
    @Inject
    lateinit var database: SpiritScribeDatabase
    
    override fun onCreate() {
        super.onCreate()
        // Initialize ResourceUtils with the application context
        ResourceUtils.init(this)
        
        // Set database instance early
        SpiritScribeDatabase.setInstance(database)
    }
}