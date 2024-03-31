package com.ferhatozcelik.library

import android.app.Activity
import android.util.Log
import androidx.annotation.Keep

@Keep
fun Activity.debug(message: String) {
   val className = this.javaClass.simpleName
    Log.d(className, message)
}

@Keep
fun Activity.error(message: String) {
    val className = this.javaClass.simpleName
    Log.e(className, message)
}

@Keep
fun Activity.info(message: String) {
    val className = this.javaClass.simpleName
    Log.i(className, message)
}

@Keep
fun Activity.verbose(message: String) {
    val className = this.javaClass.simpleName
    Log.v(className, message)
}

@Keep
fun Activity.warn(message: String) {
    val className = this.javaClass.simpleName
    Log.w(className, message)
}