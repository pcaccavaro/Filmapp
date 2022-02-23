package com.example.filmapp.util

import android.util.Log

fun <T: Any> T.logDTag(message: String) = Log.d(this::class.simpleName, message)
fun <T: Any> T.logETag(message: String) = Log.e(this::class.simpleName, message)