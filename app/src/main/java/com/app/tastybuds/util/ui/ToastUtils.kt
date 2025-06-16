package com.app.tastybuds.util.ui

import android.content.Context
import android.view.Gravity
import android.widget.Toast

fun Context.showDevelopmentToast() {
    val toast = Toast.makeText(this, "Still in development", Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}