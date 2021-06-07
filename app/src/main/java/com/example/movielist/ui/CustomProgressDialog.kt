package com.example.movielist.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.movielist.R

class CustomProgressDialog(context: Context) {

    private val mDialog = Dialog(context)

    init {
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.setContentView(R.layout.dialog_custom_progress)
    }

    fun showDialog() {
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.show()
    }

    fun hideDialog() {
        mDialog.dismiss()
    }

    fun isVisible(show: Boolean) {
        if(show) {
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.show()
        } else {
            mDialog.dismiss()
        }
    }
}