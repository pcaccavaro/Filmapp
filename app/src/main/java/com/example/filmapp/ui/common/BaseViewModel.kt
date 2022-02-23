package com.example.filmapp.ui.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

abstract class BaseViewModel<E: Any, A: Any>(app: Application) : AndroidViewModel(app) {
    private val _action = MutableLiveData<A>()
    val action = _action

    protected abstract fun processEvent(event: E)

    protected fun setAction(action: A) {
        _action.value = action
    }

    fun startEvent(event: E) {
        processEvent(event)
    }
}