package com.example.movielist.utils

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast

fun View.show(): View {
    if(visibility != View.VISIBLE) {
        alpha = 1f
        visibility = View.VISIBLE
    }
    return this
}

fun View.hide(): View {
    if(visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}

fun Any.showToast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, this.toString(), duration).apply { show() }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun String.isEmailValid(): Boolean {
    return !android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPasswordValid(): Boolean {
    return this.length > 7
}