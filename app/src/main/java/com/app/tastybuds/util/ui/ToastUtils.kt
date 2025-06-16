package com.app.tastybuds.util.ui

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.compose.runtime.Composable

fun Context.showDevelopmentToast() {
    val toast = Toast.makeText(this, "Still in development", Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

fun Context.showErrorToast(message: String) {
    android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
}

fun Context.showSuccessToast(message: String) {
    Toast.makeText(this, "Success: $message", Toast.LENGTH_SHORT).show()
}
