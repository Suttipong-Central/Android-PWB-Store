package cenergy.central.com.pwb_store.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

enum class Screen(val id: String) {
    LOGIN("Login"),
}

class Analytics(context: Context) {
    private var firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private var context: Context? = context

    // region track screen
    fun trackScreen(screen: Screen) {
        firebaseAnalytics.setCurrentScreen(context as Activity, screen.id, null)
    }

    // region track event
    private fun trackEvent(eventName: String, params: Bundle) {
        firebaseAnalytics.logEvent(eventName, params)
    }
}