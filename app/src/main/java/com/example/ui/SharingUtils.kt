package com.example.ui

import android.content.Context
import android.content.Intent

object SharingUtils {
    /**
     * Share content via the standard platform share sheet chooser.
     */
    fun shareText(context: Context, text: String, title: String = "Share via") {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, title)
            // Start activity from outside an Activity if necessary
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        } catch (_: Exception) {
            // Graceful fallback
        }
    }
}
