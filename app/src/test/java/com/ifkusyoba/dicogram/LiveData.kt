@file:Suppress("UNCHECKED_CAST")

package com.ifkusyoba.dicogram

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

// *Mengikuti prosedur pada modul Latihan Unit Testing LiveData
fun <T> LiveData<T>.getOrAwaitValue(
    timeout: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    callback: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : androidx.lifecycle.Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    try {
        callback.invoke()
        if (!latch.await(timeout, timeUnit)) {
            throw TimeoutException("Livedata never get value")
        }
    } finally {
        this.removeObserver(observer)
    }
    return data as T
}

suspend fun <T> LiveData<T>.observeForTesting(block: suspend  () -> Unit) {
    val observer = Observer<T> { }
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}