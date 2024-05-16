package com.lolo.io.onelist.core.testing

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.test.runner.AndroidJUnitRunner
import com.anggrayudi.storage.SimpleStorageHelper

class OneListTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}

class TestApplication : Application()

class TestActivityWithSimpleStorage : FragmentActivity() {
    val simpleStorage = SimpleStorageHelper(this)
}
