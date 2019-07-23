package com.lolo.io.onelist

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, OneListFragment(), "OneListFragment").commit()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        supportFragmentManager.fragments.filter { it is OnDispatchTouchEvent }.forEach {
            (it as OnDispatchTouchEvent).onDispatchTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    interface OnDispatchTouchEvent {
        fun onDispatchTouchEvent(ev: MotionEvent)
    }
}