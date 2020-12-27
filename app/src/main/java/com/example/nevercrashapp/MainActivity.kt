package com.example.nevercrashapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val tag = "NEVER_CRASH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "MainActivity onCreate")
        text.text = "点击进入crash保护测试"
        text.setOnClickListener {
            startActivity(Intent(this, Main2Activity::class.java))
        }
    }
}