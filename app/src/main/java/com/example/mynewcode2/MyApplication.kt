package com.example.mynewcode2

import android.app.Application
import org.threeten.bp.zone.ZoneRulesProvider
import com.jakewharton.threetenabp.AndroidThreeTen

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // ThreeTenABP 초기화
        AndroidThreeTen.init(this)
    }
}
