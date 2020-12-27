package com.example.nevercrashapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import kotlin.system.exitProcess

class MainApplication : Application() {
    private val tag = "NEVER_CRASH"

    override fun onCreate() {
        super.onCreate()
        registerLifeCycle()
        openCrashProtected()
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            //捕获到异常，只打印日志，不杀进程
            Log.e(tag, "${Thread.currentThread().name} 捕获到异常：${e.message}")

            //dump各个线程
            dumpAllThreadsInfo()

            //执行完方法后，线程无法继续存活。
            //如果是子线程，app不会crash。如果是主线程，就退出进程。
            if (Looper.getMainLooper() == Looper.myLooper()) {
                Process.killProcess(Process.myPid())
                exitProcess(10)
            }
        }
    }

    private fun openCrashProtected() {
        Log.d(tag, "openCrashProtected")
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                    Log.d(tag, "main looper execute loop")
                } catch (e: Throwable) {
                    Log.e(tag, "catch exception: " + e.message)
                    //主线程出现异常，关闭栈顶activity
                    ActivityStack.Instance().curr()?.finish()
                }
            }
        }
    }

    private fun registerLifeCycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                ActivityStack.Instance().push(activity)
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                ActivityStack.Instance().pop(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityStopped(activity: Activity) {
            }
        })
    }

    private fun dumpAllThreadsInfo() {
        val threadSet = Thread.getAllStackTraces().keys
        for (thread in threadSet) {
            Log.d(
                tag, "dumpAllThreadsInfo thread.name = " + thread.name
                        + ";thread.state = " + thread.state
                        + ";thread.isAlive = " + thread.isAlive
                        + ";thread.isDaemon = " + thread.isDaemon
                        + ";group = " + thread.threadGroup
            )
        }
    }
}