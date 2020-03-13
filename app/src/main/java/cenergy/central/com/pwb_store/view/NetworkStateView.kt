package cenergy.central.com.pwb_store.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import cenergy.central.com.pwb_store.R

class NetworkStateView : LinearLayout {
    private val TAG = NetworkStateView::class
    private var slideOut: Animation? = null
    private var slideIn: Animation? = null

    private lateinit var root: LinearLayout
    private lateinit var statusText: TextView

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in)
        slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_out)

        val view = View.inflate(context, R.layout.widget_network_view, this)
        root = view.findViewById(R.id.root)
        statusText = view.findViewById(R.id.statusText)
        this.visibility = View.GONE
    }

    fun onError() {
        root.background = ContextCompat.getDrawable(context, R.color.network_disconnected)
        statusText.text = context.getString(R.string.network_connect_error)
        enterAnimation(this)
    }

    fun onConnected() {
        root.background = ContextCompat.getDrawable(context, R.color.network_connected)
        statusText.text = context.getString(R.string.network_state_connected)
        Handler(Looper.getMainLooper()).postDelayed({
            exitAnimation(this)
        }, 1200)

    }

    fun onConnecting() {
        root.background = ContextCompat.getDrawable(context, R.color.network_connecting)
        statusText.text = context.getString(R.string.network_state_connecting)
        enterAnimation(this)
    }

    fun onDisconnected() {
        root.background = ContextCompat.getDrawable(context, R.color.network_disconnected)
        statusText.text = context.getString(R.string.network_disconnected)
        enterAnimation(this)
    }

    private fun enterAnimation(enterView: View?) {
        if (enterView == null)
            return
        if (enterView.visibility == View.VISIBLE) return
        enterView.visibility = View.VISIBLE
        enterView.startAnimation(slideIn)
    }

    private fun exitAnimation(exitView: View?) {
        if (exitView == null)
            return
        exitView.startAnimation(slideOut)
        slideOut?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                exitView.visibility = View.GONE
                slideOut?.setAnimationListener(null)
            }

        })
    }
}
