package com.example.nevercrashapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_2.*

class Main2Activity : AppCompatActivity() {
    private val tag = "NEVER_CRASH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)
        Log.d(tag, "Main2Activity onCreate")
        mainThreadCrash.setOnClickListener {
            makeMainThreadCrash()
        }
        subThreadCrash.setOnClickListener {
            makeSubThreadCrash()
        }
    }

    private fun makeMainThreadCrash() {
        crash()
    }

    private fun makeSubThreadCrash() {
        Thread { crash() }.start()
    }

    private fun crash() {
        val array = arrayOf(1, 2, 3)
        Log.d(tag, array[3].toString())
    }
}