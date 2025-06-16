package com.app.tastybuds.util.ui

import android.content.Context
import android.view.Gravity
import android.widget.Toast

fun Context.showDevelopmentToast() {
    val toast = Toast.makeText(this, "Still in development", Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

fun Context.showErrorToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showSuccessToast(message: String) {
    Toast.makeText(this, "Success: $message", Toast.LENGTH_SHORT).show()
}
