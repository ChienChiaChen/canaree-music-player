package dev.olog.msc.presentation.base.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.showKeyboard() {
    isFocusable = true
    if (requestFocus()) {
        val context = context
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun EditText.hideKeyboard() {
    val context = context
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    clearFocus()
    inputManager.hideSoftInputFromWindow(windowToken, 0)
}