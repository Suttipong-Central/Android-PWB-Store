package cenergy.central.com.pwb_store.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import kotlinx.android.synthetic.main.widget_watermark_view.view.*

class VersionWatermarkView : ConstraintLayout {
    private val TAG = VersionWatermarkView::class

    private lateinit var root: ConstraintLayout
    private lateinit var tvBusiness: TextView
    private lateinit var tvBuildVersion: TextView
    private lateinit var tvEnvironment: TextView

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        prepareView()
        notifyAttributeChanged()
    }

    private fun prepareView() {
        val view = View.inflate(context, R.layout.widget_watermark_view, this)
        root = view.root
        tvBusiness = view.tvBusiness
        tvBuildVersion = view.tvBuildVersion
        tvEnvironment = view.tvEnvironment

        when (BuildConfig.IS_PRODUCTION) {
            true -> {
                tvBusiness.visibility = View.GONE
                tvBuildVersion.visibility = View.VISIBLE
                tvEnvironment.visibility = View.GONE
            }
            else -> {
                tvBusiness.visibility = View.VISIBLE
                tvBuildVersion.visibility = View.VISIBLE
                tvEnvironment.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun notifyAttributeChanged() {
        tvBusiness.text = context.getText(R.string.watermark_business)
        tvEnvironment.text = context.getText(R.string.watermark_environment)
        tvBuildVersion.text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
    }
}
