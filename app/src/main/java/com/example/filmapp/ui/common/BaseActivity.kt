package com.example.filmapp.ui.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity<E : Any, A : Any> : AppCompatActivity() {
    protected abstract val viewModel: BaseViewModel<E, A>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.action.observe(this) { processAction(it) }
    }

    protected abstract fun processAction(action: A)
}