package com.lolo.io.onelist.core.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class OneListTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}

class TestApplication : Application()